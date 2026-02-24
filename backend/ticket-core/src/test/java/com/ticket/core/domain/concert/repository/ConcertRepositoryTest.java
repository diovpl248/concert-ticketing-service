package com.ticket.core.domain.concert.repository;


import com.ticket.core.domain.concert.entity.Concert;
import com.ticket.core.domain.concert.entity.ConcertCategory;
import com.ticket.core.domain.concert.entity.ConcertStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Test
    @DisplayName("Concert 엔티티 저장 및 조회 테스트")
    void saveAndFindConcert() {
        // given
        Concert concert = Concert.builder()
                .title("아이유 2024 콘서트")
                .category(ConcertCategory.CONCERT)
                .description("상암 월드컵 경기장")
                .thumbnailUrl("http://image.url")
                .venue("서울 월드컵 경기장")
                .status(ConcertStatus.OPEN)
                .build();

        // when
        Concert savedConcert = concertRepository.save(concert);
        Optional<Concert> foundConcert = concertRepository.findById(savedConcert.getId());

        // then
        assertThat(foundConcert).isPresent();
        assertThat(foundConcert.get().getTitle()).isEqualTo("아이유 2024 콘서트");
    }
}
