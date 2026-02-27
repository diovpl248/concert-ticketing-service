package com.ticket.app.application;

import com.ticket.app.dto.booking.BookingRequest;
import com.ticket.app.dto.booking.BookingResponse;
import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.booking.entity.Booking;
import com.ticket.core.domain.booking.service.BookingService;
import com.ticket.core.domain.queue.repository.QueueRedisRepository;
import com.ticket.core.domain.seat.entity.Seat;
import com.ticket.core.domain.seat.service.SeatService;
import com.ticket.core.domain.user.entity.User;
import com.ticket.core.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingUseCase {

    private final SeatService seatService;
    private final BookingService bookingService;
    private final QueueRedisRepository queueRedisRepository;
    private final UserRepository userRepository; // 사용자 정보 조회를 위한 선택적 의존성

    public BookingResponse createBooking(BookingRequest request, String queueToken, Long userId) {
        // 1. 대기열 토큰 검증
        if (!queueRedisRepository.isActiveToken(request.concertId(), queueToken)) {
            throw new BusinessException(ErrorCode.NOT_ACTIVE_QUEUE);
        }

        // 2. 분산 락을 이용한 좌석 선점 및 레디스 점유 등록
        seatService.reserveSeat(request.seatId(), userId);

        // 3. 임시 예약 정보 생성 (현재는 임시 사용자로 처리하거나 userId로 조회)
        User user = userRepository.findById(userId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("Test User " + userId)
                        .build())); // 개발 편의를 위한 임시 생성

        Seat seat = seatService.getSeat(request.seatId());
        Booking booking = bookingService.createBooking(user, seat);

        return BookingResponse.from(booking);
    }
}
