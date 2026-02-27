package com.ticket.app.dto.payment;

import com.ticket.core.domain.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull Long concertId,
        @NotNull Long bookingId,
        @NotNull PaymentMethod paymentMethod,
        @NotNull Integer amount
) {
}
