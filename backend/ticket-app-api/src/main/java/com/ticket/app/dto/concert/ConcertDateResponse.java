package com.ticket.app.dto.concert;

import com.ticket.core.domain.concert.entity.ConcertDate;
import java.time.LocalDateTime;

public record ConcertDateResponse(
        Long id,
        LocalDateTime datetime
) {
    public static ConcertDateResponse from(ConcertDate concertDate) {
        return new ConcertDateResponse(
                concertDate.getId(),
                concertDate.getConcertDatetime()
        );
    }
}
