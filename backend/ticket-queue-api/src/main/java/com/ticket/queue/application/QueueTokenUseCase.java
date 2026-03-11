package com.ticket.queue.application;

import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.queue.entity.QueueStatus;
import com.ticket.core.domain.queue.repository.QueueRedisRepository;
import com.ticket.queue.dto.QueueStatusResponse;
import com.ticket.queue.dto.QueueTokenRequest;
import com.ticket.queue.dto.QueueTokenResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueTokenUseCase {

    private final QueueRedisRepository queueRedisRepository;

    private static final long MAX_ACTIVE_USERS = 100L;

    /**
     * 토큰 발급 및 대기열/활성열 진입
     */
    public QueueTokenResponse issueToken(QueueTokenRequest request, Long userId) {
        String token = generateToken(userId);
        Long concertId = request.concertId();

        // 현재 활성열의 사용자 수 확인
        Long activeCount = queueRedisRepository.getActiveQueueSize(concertId);

        if (activeCount < MAX_ACTIVE_USERS) {
            // 활성열 여유가 있으면 바로 활성 상태로 진입 (5분 만료)
            queueRedisRepository.addActiveQueue(concertId, token, 300L);
            return new QueueTokenResponse(token, QueueStatus.ACTIVE, 0L, 0L);
        } else {
            // 여유가 없으면 대기열로 진입 (현재 시간을 score로 사용)
            double currentTimeMillis = System.currentTimeMillis();
            queueRedisRepository.addWaitQueue(concertId, token, currentTimeMillis);
            
            // 진입 후 현재 내 예상 대기 순번 조회
            Optional<Long> rankOpt = queueRedisRepository.getWaitRank(concertId, token);
            Long position = rankOpt.map(r -> r + 1).orElse(0L);
            Long estimatedWaitTime = (position / 10) + 1;
            
            return new QueueTokenResponse(token, QueueStatus.WAITING, position, estimatedWaitTime);
        }
    }

    /**
     * 현재 토큰의 대기 상태 조회 (Polling)
     */
    public QueueStatusResponse getStatus(Long concertId, String token) {
        // 1. 활성 상태인지 먼저 확인
        if (queueRedisRepository.isActiveToken(concertId, token)) {
            return new QueueStatusResponse(token, QueueStatus.ACTIVE, 0L, 0L);
        }

        // 2. 대기열에 있는지 랭크 확인
        Optional<Long> rankOpt = queueRedisRepository.getWaitRank(concertId, token);
        
        if (rankOpt.isPresent()) {
            Long rank = rankOpt.get(); // 0부터 시작하는 인덱스
            Long position = rank + 1;
            // 예상 대기 시간 등 계산 (예: 1초당 10명 입장 가정)
            long estimatedWaitTime = (position / 10) + 1; 
            
            return new QueueStatusResponse(token, QueueStatus.WAITING, position, estimatedWaitTime);
        }

        // 3. 토큰이 무효하거나 만료됨
        throw new BusinessException(
            ErrorCode.QUEUE_TOKEN_NOT_FOUND
        );
    }

    private String generateToken(Long userId) {
        // 실제 환경에서는 UUID 발급 후 JWT 등과 매핑하거나 함께 활용해야 합니다.
        return UUID.randomUUID().toString() + "-" + userId;
    }
}
