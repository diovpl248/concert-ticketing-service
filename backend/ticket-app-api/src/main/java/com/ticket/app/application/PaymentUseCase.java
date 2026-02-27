package com.ticket.app.application;

import com.ticket.app.dto.payment.PaymentRequest;
import com.ticket.app.dto.payment.PaymentResponse;
import com.ticket.core.common.exception.BusinessException;
import com.ticket.core.common.exception.ErrorCode;
import com.ticket.core.domain.booking.entity.Booking;
import com.ticket.core.domain.booking.service.BookingService;
import com.ticket.core.domain.payment.entity.Payment;
import com.ticket.core.domain.payment.service.PaymentService;
import com.ticket.core.domain.payment.service.PaymentService;
import com.ticket.core.domain.queue.repository.QueueRedisRepository;
import com.ticket.core.domain.seat.service.SeatService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentUseCase {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final SeatService seatService;
    private final QueueRedisRepository queueRedisRepository;

    public PaymentResponse processPayment(PaymentRequest request, String queueToken) {
        // 1. 대기열 토큰 검증
        if (!queueRedisRepository.isActiveToken(request.concertId(), queueToken)) {
            throw new BusinessException(ErrorCode.NOT_ACTIVE_QUEUE);
        }

        // 2. 예약 상태 변경 (결제 완료)
        bookingService.completeBooking(request.bookingId());
        Booking booking = bookingService.getBooking(request.bookingId());

        // 3. 결제 처리 및 내역 저장
        Payment payment = paymentService.processPayment(booking, request.paymentMethod(), request.amount());

        // 4. 대기열 토큰 만료 처리 (사용 완료)
        queueRedisRepository.removeActiveQueue(request.concertId(), queueToken);

        // 5. 비즈니스 좌석 락 해제 (임시 점유 -> 결제 확정됨)
        seatService.releaseSeatLock(booking.getSeat().getId());

        return PaymentResponse.from(payment);
    }
}
