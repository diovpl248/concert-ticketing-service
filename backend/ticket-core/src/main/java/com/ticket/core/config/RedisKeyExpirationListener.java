package com.ticket.core.config;

import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.seat.entity.Seat;
import com.ticket.core.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final SeatRepository seatRepository;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, SeatRepository seatRepository) {
        super(listenerContainer);
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("Redis key expired: {}", expiredKey);

        if (expiredKey.startsWith("seat:held:")) {
            try {
                Long seatId = Long.parseLong(expiredKey.split(":")[2]);
                releaseSeat(seatId);
            } catch (NumberFormatException e) {
                log.error("Failed to parse seatId from expired key: {}", expiredKey, e);
            }
        }
    }

    private void releaseSeat(Long seatId) {
        seatRepository.findById(seatId).ifPresent(seat -> {
            try {
                seat.release();
                seatRepository.save(seat);
                log.info("Seat {} released due to business lock expiration", seatId);
            } catch (Exception e) {
               log.error("Failed to release seat {}", seatId, e);
            }
        });
    }
}
