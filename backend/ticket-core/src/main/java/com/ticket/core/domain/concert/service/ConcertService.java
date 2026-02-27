package com.ticket.core.domain.concert.service;

import com.ticket.core.domain.concert.entity.Concert;
import com.ticket.core.domain.concert.entity.ConcertDate;
import com.ticket.core.domain.concert.entity.ConcertStatus;
import com.ticket.core.domain.concert.repository.ConcertDateRepository;
import com.ticket.core.domain.concert.repository.ConcertRepository;
import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertDateRepository concertDateRepository;

    public List<Concert> getAvailableConcerts() {
        return concertRepository.findByStatus(ConcertStatus.OPEN);
    }

    public Concert getConcert(Long concertId) {
        return concertRepository.findById(concertId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONCERT_NOT_FOUND));
    }

    public List<ConcertDate> getAvailableDates(Long concertId) {
        // validate concert exists
        getConcert(concertId);
        return concertDateRepository.findByConcertId(concertId);
    }

    public ConcertDate getConcertDate(Long dateId) {
        return concertDateRepository.findById(dateId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONCERT_DATE_NOT_FOUND));
    }
}
