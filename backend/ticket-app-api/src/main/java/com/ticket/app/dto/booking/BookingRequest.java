package com.ticket.app.dto.booking;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull Long concertId,
        @NotNull Long dateId,
        @NotNull Long seatId
) {
}
