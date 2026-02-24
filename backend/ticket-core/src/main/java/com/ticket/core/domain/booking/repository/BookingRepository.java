package com.ticket.core.domain.booking.repository;

import com.ticket.core.domain.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
