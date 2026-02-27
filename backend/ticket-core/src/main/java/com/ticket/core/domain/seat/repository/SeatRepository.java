package com.ticket.core.domain.seat.repository;

import com.ticket.core.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByConcertDateId(Long concertDateId);
}
