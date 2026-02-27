package com.ticket.app.controller;

import com.ticket.app.application.ConcertUseCase;
import com.ticket.app.dto.concert.ConcertDateResponse;
import com.ticket.app.dto.concert.ConcertResponse;
import com.ticket.app.dto.concert.SeatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertUseCase concertUseCase;

    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getConcerts() {
        return ResponseEntity.ok(concertUseCase.getAvailableConcerts());
    }

    @GetMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> getConcert(@PathVariable Long concertId) {
        return ResponseEntity.ok(concertUseCase.getConcert(concertId));
    }

    @GetMapping("/{concertId}/dates/{dateId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(
            @PathVariable Long concertId, 
            @PathVariable Long dateId,
            @RequestHeader(value = "Queue-Token") String queueToken) {
        // 여기서 인터셉터나 UseCase를 통해 대기열 토큰이 활성 상태인지 검증할 수 있습니다.
        return ResponseEntity.ok(concertUseCase.getSeats(concertId, dateId));
    }
}
