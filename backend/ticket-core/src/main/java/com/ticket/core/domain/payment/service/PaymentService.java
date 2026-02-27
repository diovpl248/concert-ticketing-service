package com.ticket.core.domain.payment.service;

import com.ticket.core.domain.booking.entity.Booking;
import com.ticket.core.domain.payment.entity.Payment;
import com.ticket.core.domain.payment.entity.PaymentMethod;
import com.ticket.core.domain.payment.entity.PaymentStatus;
import com.ticket.core.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(Booking booking, PaymentMethod method, Integer amount) {
        // Mock 결제 로직 - 현재는 항상 성공하는 것으로 간주합니다.
        
        String ticketCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(amount)
                .paymentMethod(method)
                .status(PaymentStatus.PAID)
                .ticketCode(ticketCode)
                .issuedAt(LocalDateTime.now())
                .build();
                
        return paymentRepository.save(payment);
    }
}
