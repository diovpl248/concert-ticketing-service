package com.ticket.app.dto.concert;

import com.ticket.core.domain.seat.entity.Seat;

public record SeatResponse(
        Long id,
        String section,
        String rowNo,
        String colNo,
        Integer price,
        String status
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSection(),
                seat.getRowNo(),
                seat.getColNo(),
                seat.getPrice(),
                seat.getStatus().name()
        );
    }
}
