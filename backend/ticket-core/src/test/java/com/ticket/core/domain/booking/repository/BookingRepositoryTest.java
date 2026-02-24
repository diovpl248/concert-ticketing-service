package com.ticket.core.domain.booking.repository;


import com.ticket.core.domain.booking.entity.Booking;
import com.ticket.core.domain.booking.entity.BookingStatus;
import com.ticket.core.domain.concert.entity.Concert;
import com.ticket.core.domain.concert.entity.ConcertCategory;
import com.ticket.core.domain.concert.entity.ConcertDate;
import com.ticket.core.domain.concert.entity.ConcertStatus;
import com.ticket.core.domain.concert.repository.ConcertDateRepository;
import com.ticket.core.domain.concert.repository.ConcertRepository;
import com.ticket.core.domain.seat.entity.Seat;
import com.ticket.core.domain.seat.entity.SeatStatus;
import com.ticket.core.domain.seat.repository.SeatRepository;
import com.ticket.core.domain.user.entity.User;
import com.ticket.core.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private ConcertDateRepository concertDateRepository;

    @Test
    @DisplayName("Booking 엔티티 저장 및 조회 테스트")
    void saveAndFindBooking() {
        // given
        User user = userRepository.save(User.builder().username("testuser").build());

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

        Seat seat = seatRepository.save(Seat.builder()
                .concertDate(concertDate)
                .section("VIP")
                .rowNo("A")
                .colNo("1")
                .price(150000)
                .status(SeatStatus.AVAILABLE)
                .build());

        Booking booking = Booking.builder()
                .user(user)
                .seat(seat)
                .status(BookingStatus.HELD)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // when
        Booking savedBooking = bookingRepository.save(booking);

        // then
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedBooking.getSeat().getId()).isEqualTo(seat.getId());
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.HELD);
    }
}
