package com.ticket.app.application;

import com.ticket.app.dto.concert.ConcertDateResponse;
import com.ticket.app.dto.concert.ConcertResponse;
import com.ticket.app.dto.concert.SeatResponse;
import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.concert.entity.ConcertDate;
import com.ticket.core.domain.concert.service.ConcertService;
import com.ticket.core.domain.queue.repository.QueueRedisRepository;
import com.ticket.core.domain.seat.service.SeatService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertUseCase {

    private final ConcertService concertService;
    private final SeatService seatService;
	private final QueueRedisRepository queueRedisRepository;


    public List<ConcertResponse> getAvailableConcerts() {
        return concertService.getAvailableConcerts().stream()
                .map(concert -> ConcertResponse.from(concert, null))
                .collect(Collectors.toList());
    }

    public ConcertResponse getConcert(Long concertId) {
        List<ConcertDateResponse> dates = getAvailableDates(concertId);
        return ConcertResponse.from(concertService.getConcert(concertId), dates);
    }

    public List<ConcertDateResponse> getAvailableDates(Long concertId) {
        return concertService.getAvailableDates(concertId).stream()
                .map(ConcertDateResponse::from)
                .collect(Collectors.toList());
    }

    public List<SeatResponse> getSeats(Long concertId, Long dateId, String queueToken) {
        // 대기열 토큰 검증
        if (!queueRedisRepository.isActiveToken(concertId, queueToken)) {
            throw new BusinessException(ErrorCode.NOT_ACTIVE_QUEUE);
        }

        ConcertDate date = concertService.getConcertDate(dateId);
        if (!date.getConcert().getId().equals(concertId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return seatService.getSeats(dateId).stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());
    }
}
