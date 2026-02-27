package com.ticket.app.dto.booking;

import com.ticket.core.domain.booking.entity.Booking;

import java.time.LocalDateTime;

public record BookingResponse(
        Long bookingId,
        Long seatId,
        String status,
        LocalDateTime expiresAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSeat().getId(),
                booking.getStatus().name(),
                booking.getExpiresAt()
        );
    }
}
