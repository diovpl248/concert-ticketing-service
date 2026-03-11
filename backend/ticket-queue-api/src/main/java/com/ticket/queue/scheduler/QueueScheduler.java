package com.ticket.queue.scheduler;

import com.ticket.core.domain.queue.repository.QueueRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueRedisRepository queueRedisRepository;

    // TODO: 다건 공연 지원 시 DB 조회 등 동적 처리 필요. MVP용 기본 ID 할당
    private static final Long DEFAULT_CONCERT_ID = 1L;
    private static final long MAX_ACTIVE_USERS = 1000L;
    private static final long BATCH_SIZE = 1000L;

    /**
     * 주기적으로 대기열(WAITING)에서 가장 오래 대기한 사용자를 활성열(ACTIVE)로 전환
     * (3초 마다 실행)
     */
    @Scheduled(fixedRate = 3000)
    public void activateWaitingTokens() {
        Long activeCount = queueRedisRepository.getActiveQueueSize(DEFAULT_CONCERT_ID);
        long availableSlots = MAX_ACTIVE_USERS - activeCount;

        if (availableSlots > 0) {
            long countToActivate = Math.min(availableSlots, BATCH_SIZE);
            Set<String> tokensToActivate = queueRedisRepository.popMinWaitQueue(DEFAULT_CONCERT_ID, countToActivate);

            if (!tokensToActivate.isEmpty()) {
                queueRedisRepository.addActiveQueue(DEFAULT_CONCERT_ID, tokensToActivate);
                log.info("Activated {} tokens for Concert ID {}", tokensToActivate.size(), DEFAULT_CONCERT_ID);
            }
        }
    }
}
