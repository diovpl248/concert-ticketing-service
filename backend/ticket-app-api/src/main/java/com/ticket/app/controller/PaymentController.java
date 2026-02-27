package com.ticket.app.controller;

import com.ticket.app.application.PaymentUseCase;
import com.ticket.app.dto.payment.PaymentRequest;
import com.ticket.app.dto.payment.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestHeader(value = "Queue-Token") String queueToken,
            @Valid @RequestBody PaymentRequest request) {
		Long userId = 1L; // TODO: 인증 정보에서 가져오도록 변경
        PaymentResponse response = paymentUseCase.processPayment(request, queueToken, userId);
        return ResponseEntity.ok(response);
    }
}
