package com.ticket.app.controller;

import com.ticket.app.application.BookingUseCase;
import com.ticket.app.dto.booking.BookingRequest;
import com.ticket.app.dto.booking.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingUseCase bookingUseCase;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader(value = "Queue-Token") String queueToken,
            @Valid @RequestBody BookingRequest request) {
        // 임시로 userId 1L을 사용합니다. 실제로는 인증 토큰에서 추출해야 합니다.
        Long userId = 1L; 
        BookingResponse response = bookingUseCase.createBooking(request, queueToken, userId);
        return ResponseEntity.ok(response);
    }
}
