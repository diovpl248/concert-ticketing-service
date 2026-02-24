package com.ticket.core.domain.concert.repository;

import com.ticket.core.domain.concert.entity.ConcertDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertDateRepository extends JpaRepository<ConcertDate, Long> {
}
