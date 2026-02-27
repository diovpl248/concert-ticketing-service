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
import com.ticket.core.domain.seat.repository.SeatRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

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
    public Booking validateAndGetBooking(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_BOOKING);
        }
        
        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            booking.cancel();
            
            // 좌석 상태도 함께 다시 AVAILABLE로 변경
            Seat seat = booking.getSeat();
            seat.release();
            seatRepository.save(seat);

            throw new BusinessException(ErrorCode.BOOKING_EXPIRED);
        }
        return booking;
    }

    @Transactional
    public void completeBooking(Booking booking) {
        booking.markAsPaid();
        
        // 결제 완료에 따른 좌석 상태 변경 (HELD -> BOOKED)
        Seat seat = booking.getSeat();
        seat.book();
        seatRepository.save(seat);
    }
}
