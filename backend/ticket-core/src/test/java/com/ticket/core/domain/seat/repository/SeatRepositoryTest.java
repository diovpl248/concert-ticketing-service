package com.ticket.core.domain.seat.repository;


import com.ticket.core.domain.concert.entity.Concert;
import com.ticket.core.domain.concert.entity.ConcertCategory;
import com.ticket.core.domain.concert.entity.ConcertDate;
import com.ticket.core.domain.concert.entity.ConcertStatus;
import com.ticket.core.domain.concert.repository.ConcertDateRepository;
import com.ticket.core.domain.concert.repository.ConcertRepository;
import com.ticket.core.domain.seat.entity.Seat;
import com.ticket.core.domain.seat.entity.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertDateRepository concertDateRepository;

    @Test
    @DisplayName("Seat 엔티티 저장 및 조회 테스트")
    void saveAndFindSeat() {
        // given
        Concert concert = concertRepository.save(Concert.builder()
                .title("테스트 콘서트")
                .category(ConcertCategory.CONCERT)
                .venue("테스트 공연장")
                .status(ConcertStatus.OPEN)
                .build());

        ConcertDate concertDate = concertDateRepository.save(ConcertDate.builder()
                .concert(concert)
                .concertDatetime(LocalDateTime.now().plusDays(10))
                .build());

        Seat seat = Seat.builder()
                .concertDate(concertDate)
                .section("VIP")
                .rowNo("A")
                .colNo("1")
                .price(150000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // when
        Seat savedSeat = seatRepository.save(seat);

        // then
        assertThat(savedSeat.getId()).isNotNull();
        assertThat(savedSeat.getConcertDate().getId()).isEqualTo(concertDate.getId());
        assertThat(savedSeat.getVersion()).isEqualTo(0L); // @Version 초기값
    }
}
