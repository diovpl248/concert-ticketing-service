package com.ticket.core.domain.seat.service;

import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.seat.entity.Seat;
import com.ticket.core.domain.seat.entity.SeatStatus;
import com.ticket.core.domain.seat.repository.SeatRepository;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    @Transactional(readOnly = true)
    public List<Seat> getSeats(Long concertDateId) {
        return seatRepository.findByConcertDateId(concertDateId);
    }

    @Transactional(readOnly = true)
    public Seat getSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEAT_NOT_FOUND));
    }

    /**
     * Redisson 기반 분산 락을 이용한 좌석 선점 로직
     * 트랜잭션 경계를 프로그래밍 방식으로 처리하여 락이 트랜잭션을 감싸도록 보장합니다.
     */
    public void reserveSeat(Long seatId, Long userId) {
        String lockKey = "seat:lock:" + seatId;
        String heldKey = "seat:held:" + seatId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 획득 시도 (대기 최대 5초, 유지 3초)
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                // 트래픽 초과 또는 락 획득 실패 시 커스텀 예외 발생
                throw new BusinessException(ErrorCode.SEAT_ALREADY_BOOKED); // 또는 일반적인 CONFLICT 처리
            }

            // 트랜잭션을 수동으로 열어 락 안에서 수행되도록 보장
            transactionTemplate.executeWithoutResult(status -> {
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.SEAT_NOT_FOUND));

                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new BusinessException(ErrorCode.SEAT_ALREADY_BOOKED);
                }

                seat.reserve();
                seatRepository.saveAndFlush(seat); // 명시적 flush로 트랜잭션 종료 전 DB 반영 확인
            });

            // 3. 비즈니스 락 (임시 배정) 정보를 Redis에 저장 (5분 TTL)
            RBucket<Long> heldBucket = redissonClient.getBucket(heldKey);
            heldBucket.set(userId, 5, TimeUnit.MINUTES;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 결제 완료 또는 취소 시 좌석 점유 권한(비즈니스 락)을 해제합니다.
     */
    public void releaseSeatLock(Long seatId) {
        String heldKey = "seat:held:" + seatId;
        RBucket<Long> heldBucket = redissonClient.getBucket(heldKey);
        heldBucket.delete();
    }
}
