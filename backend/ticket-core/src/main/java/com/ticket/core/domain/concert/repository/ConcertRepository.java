package com.ticket.core.domain.concert.repository;

import com.ticket.core.domain.concert.entity.Concert;
import com.ticket.core.domain.concert.entity.ConcertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    List<Concert> findByStatus(ConcertStatus status);
}
