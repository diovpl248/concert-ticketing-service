package com.ticket.app.dto.concert;

import com.ticket.core.domain.concert.entity.Concert;
import java.util.List;

public record ConcertResponse(
        Long id,
        String title,
        String category,
        String thumbnailUrl,
        String venue,
        List<ConcertDateResponse> dates
) {
    public static ConcertResponse from(Concert concert, List<ConcertDateResponse> dates) {
        return new ConcertResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getCategory().name(),
                concert.getThumbnailUrl(),
                concert.getVenue(),
                dates
        );
    }
}
