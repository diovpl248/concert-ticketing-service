package com.ticket.core.domain.payment.entity;

import com.ticket.core.domain.booking.entity.Booking;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "ticket_code", nullable = false, length = 100)
    private String ticketCode;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Builder
    public Payment(Booking booking, Integer amount, PaymentMethod paymentMethod, PaymentStatus status, String ticketCode, LocalDateTime issuedAt) {
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.ticketCode = ticketCode;
        this.issuedAt = issuedAt;
    }
}
