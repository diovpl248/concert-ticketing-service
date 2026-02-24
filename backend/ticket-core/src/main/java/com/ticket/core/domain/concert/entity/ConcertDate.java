package com.ticket.core.domain.concert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "concert_dates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "concert_datetime", nullable = false)
    private LocalDateTime concertDatetime;

    @Builder
    public ConcertDate(Concert concert, LocalDateTime concertDatetime) {
        this.concert = concert;
        this.concertDatetime = concertDatetime;
    }
}
