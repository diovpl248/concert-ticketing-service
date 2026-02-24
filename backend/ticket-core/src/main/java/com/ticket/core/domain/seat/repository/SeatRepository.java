package com.ticket.core.domain.seat.repository;

import com.ticket.core.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
