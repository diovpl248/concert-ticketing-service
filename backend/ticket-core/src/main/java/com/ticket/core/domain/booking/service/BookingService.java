package com.ticket.core.domain.booking.service;

import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.booking.entity.Booking;
import com.ticket.core.domain.booking.entity.BookingStatus;
import com.ticket.core.domain.booking.repository.BookingRepository;
import com.ticket.core.domain.seat.entity.Seat;
import com.ticket.core.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    @Transactional
    public Booking createBooking(User user, Seat seat) {
        // 예약 만료 시간은 현재로부터 5분 후로 설정
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        
        Booking booking = Booking.builder()
                .user(user)
                .seat(seat)
                .status(BookingStatus.HELD)
                .expiresAt(expiresAt)
                .build();
                
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
    }

    @Transactional
    public void completeBooking(Long bookingId) {
        Booking booking = getBooking(bookingId);
        
        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            booking.cancel();
            // 실제 환경에서는 여기서 좌석도 다시 해제해야 합니다.
            // 하지만 보통 이 부분은 UseCase나 도메인 이벤트에 의해 처리됩니다.
            throw new BusinessException(ErrorCode.BOOKING_NOT_FOUND); // 커스텀 에러 사용
        }
        
        booking.markAsPaid();
    }
}
