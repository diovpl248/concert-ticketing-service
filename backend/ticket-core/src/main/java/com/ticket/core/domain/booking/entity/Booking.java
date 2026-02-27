package com.ticket.core.domain.booking.entity;


import com.ticket.core.domain.seat.entity.Seat;
import com.ticket.core.domain.user.entity.User;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Builder
    public Booking(User user, Seat seat, BookingStatus status, LocalDateTime expiresAt) {
        this.user = user;
        this.seat = seat;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public void markAsPaid() {
        if (this.status != BookingStatus.HELD) {
            throw new IllegalStateException("Only HELD bookings can be paid.");
        }
        this.status = BookingStatus.PAID;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }
}
