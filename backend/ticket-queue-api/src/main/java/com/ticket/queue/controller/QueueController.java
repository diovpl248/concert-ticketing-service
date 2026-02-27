package com.ticket.queue.controller;

import com.ticket.queue.application.QueueTokenUseCase;
import com.ticket.queue.dto.QueueStatusResponse;
import com.ticket.queue.dto.QueueTokenRequest;
import com.ticket.queue.dto.QueueTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueTokenUseCase queueTokenUseCase;

    /**
     * 대기열 진입 및 토큰 발급 API
     */
    @PostMapping("/tokens")
    public ResponseEntity<QueueTokenResponse> issueToken(@Valid @RequestBody QueueTokenRequest request) {
        // TODO: 향후 Authorization 헤더에서 JWT 토큰을 파싱하여 실제 userId 추출 필요
        Long dummyUserId = 1L;
        QueueTokenResponse response = queueTokenUseCase.issueToken(request, dummyUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 대기열 상태 (Polling) 조회 API
     */
    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(
            // API 명세에는 없지만 내부 Redis 구조상 concertId가 필요하여 임시 유지, 혹은 명세 수정 필요
            @RequestParam Long concertId,
            @RequestHeader(value = "Queue-Token") String token) {
            
        QueueStatusResponse response = queueTokenUseCase.getStatus(concertId, token);
        return ResponseEntity.ok(response);
    }
}
