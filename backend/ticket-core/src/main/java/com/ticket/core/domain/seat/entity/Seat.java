package com.ticket.core.domain.seat.entity;

import com.ticket.core.domain.concert.entity.ConcertDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_date_id", nullable = false)
    private ConcertDate concertDate;

    @Column(nullable = false, length = 10)
    private String section;

    @Column(name = "row_no", nullable = false, length = 10)
    private String rowNo;

    @Column(name = "col_no", nullable = false, length = 10)
    private String colNo;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    @Builder
    public Seat(ConcertDate concertDate, String section, String rowNo, String colNo, Integer price, SeatStatus status) {
        this.concertDate = concertDate;
        this.section = section;
        this.rowNo = rowNo;
        this.colNo = colNo;
        this.price = price;
        this.status = status;
    }
}
