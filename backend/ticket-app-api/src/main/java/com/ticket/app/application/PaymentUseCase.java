package com.ticket.app.application;

import com.ticket.app.dto.payment.PaymentRequest;
import com.ticket.app.dto.payment.PaymentResponse;
import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.booking.entity.Booking;
import com.ticket.core.domain.booking.service.BookingService;
import com.ticket.core.domain.payment.entity.Payment;
import com.ticket.core.domain.payment.service.PaymentService;
import com.ticket.core.domain.queue.repository.QueueRedisRepository;
import com.ticket.core.domain.seat.service.SeatService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentUseCase {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final SeatService seatService;
    private final QueueRedisRepository queueRedisRepository;

	@Transactional
    public PaymentResponse processPayment(PaymentRequest request, String queueToken, Long userId) {
        // 1. 대기열 토큰 검증
        if (!queueRedisRepository.isActiveToken(request.concertId(), queueToken)) {
            throw new BusinessException(ErrorCode.NOT_ACTIVE_QUEUE);
        }

        // 2. 예약 유효성 검증 (본인 확인 및 만료 여부)
        Booking booking = bookingService.validateAndGetBooking(request.bookingId(), userId);

        // 3. 결제 처리 및 내역 저장 (가장 먼저 결제가 수행됨)
        Payment payment = paymentService.processPayment(booking, request.paymentMethod(), request.amount());

        // 4. 결제 완료에 따른 예약 및 좌석 상태 최종 반영 (HELD -> BOOKED)
        bookingService.completeBooking(booking);

        // 4. 대기열 토큰 만료 처리 (사용 완료)
        queueRedisRepository.removeActiveQueue(request.concertId(), queueToken);

        // 5. 비즈니스 좌석 락 해제 (임시 점유 -> 결제 확정됨)
        seatService.releaseSeatLock(booking.getSeat().getId());

        return PaymentResponse.from(payment);
    }
}
