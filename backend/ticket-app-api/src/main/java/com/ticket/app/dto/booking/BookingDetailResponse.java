package com.ticket.app.dto.booking;

import com.ticket.core.domain.booking.entity.Booking;
import com.ticket.core.domain.concert.entity.Concert;
import com.ticket.core.domain.concert.entity.ConcertDate;
import com.ticket.core.domain.payment.entity.Payment;
import com.ticket.core.domain.seat.entity.Seat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BookingDetailResponse(
        Long bookingId,
        ConcertInfo concert,
        SeatInfo seat,
        PaymentInfo payment
) {
    public static BookingDetailResponse of(Booking booking, Payment payment) {
        return BookingDetailResponse.builder()
                .bookingId(booking.getId())
                .concert(ConcertInfo.from(booking))
                .seat(SeatInfo.from(booking))
                .payment(payment != null ? PaymentInfo.from(payment) : null)
                .build();
    }

    @Builder
    public record ConcertInfo(
            Long id,
            String title,
            String venue,
            String thumbnailUrl,
            String date,
            String time
    ) {
        public static ConcertInfo from(Booking booking) {
            ConcertDate concertDate = booking.getSeat().getConcertDate();
            Concert concert = concertDate.getConcert();
            return ConcertInfo.builder()
                    .id(concert.getId())
                    .title(concert.getTitle())
                    .venue(concert.getVenue())
                    .thumbnailUrl(concert.getThumbnailUrl())
                    .date(concertDate.getConcertDatetime().toLocalDate().toString())
                    .time(concertDate.getConcertDatetime().toLocalTime().toString())
                    .build();
        }
    }

    @Builder
    public record SeatInfo(
            Long id,
            String section,
            String row,
            String col,
            Integer price
    ) {
        public static SeatInfo from(Booking booking) {
            Seat seat = booking.getSeat();
            return SeatInfo.builder()
                    .id(seat.getId())
                    .section(seat.getSection())
                    .row(seat.getRowNo())
                    .col(seat.getColNo())
                    .price(seat.getPrice())
                    .build();
        }
    }

    @Builder
    public record PaymentInfo(
            Long paymentId,
            Integer amount,
            String paymentMethod,
            String status,
            LocalDateTime issuedAt
    ) {
        public static PaymentInfo from(Payment payment) {
            return PaymentInfo.builder()
                    .paymentId(payment.getId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .status(payment.getStatus().name())
                    .issuedAt(payment.getIssuedAt())
                    .build();
        }
    }
}
