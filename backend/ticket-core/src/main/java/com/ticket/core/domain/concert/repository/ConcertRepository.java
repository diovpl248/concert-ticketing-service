package com.ticket.core.domain.concert.repository;

import com.ticket.core.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
