package com.ticket.app.dto.payment;

import com.ticket.core.domain.payment.entity.Payment;

public record PaymentResponse(
        Long paymentId,
        String status,
        String ticketCode
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getStatus().name(),
                payment.getTicketCode()
        );
    }
}
