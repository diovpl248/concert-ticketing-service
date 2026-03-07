package com.ticket.core.domain.payment.repository;

import com.ticket.core.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @EntityGraph(attributePaths = {"booking", "booking.seat", "booking.seat.concertDate", "booking.seat.concertDate.concert"})
    Optional<Payment> findByBookingId(Long bookingId);
}
