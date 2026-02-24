CREATE DATABASE IF NOT EXISTS `ticket` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ticket`;

-- Users Table (Minimal)
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Concerts Table
CREATE TABLE `concerts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `category` VARCHAR(50) NOT NULL COMMENT 'CONCERT, MUSICAL, CLASSIC',
    `description` TEXT,
    `thumbnail_url` VARCHAR(255),
    `venue` VARCHAR(255) NOT NULL,
    `status` VARCHAR(20) NOT NULL COMMENT 'OPEN, CLOSED',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Concert Dates Table
CREATE TABLE `concert_dates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `concert_id` BIGINT NOT NULL,
    `concert_datetime` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_concert_id` (`concert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seats Table
CREATE TABLE `seats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `concert_date_id` BIGINT NOT NULL,
    `section` VARCHAR(10) NOT NULL,
    `row_no` VARCHAR(10) NOT NULL,
    `col_no` VARCHAR(10) NOT NULL,
    `price` INT NOT NULL,
    `status` VARCHAR(20) NOT NULL COMMENT 'AVAILABLE, HELD, BOOKED',
    `version` BIGINT NOT NULL DEFAULT 0 COMMENT 'Optimistic Lock',
    PRIMARY KEY (`id`),
    KEY `idx_concert_date_id` (`concert_date_id`),
    UNIQUE KEY `ux_seat_position` (`concert_date_id`, `section`, `row_no`, `col_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bookings Table
CREATE TABLE `bookings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `seat_id` BIGINT NOT NULL,
    `status` VARCHAR(20) NOT NULL COMMENT 'HELD, PAID, CANCELLED',
    `expires_at` DATETIME,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    UNIQUE KEY `ux_seat_id` (`seat_id`) -- One active booking per seat
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments Table (Added for /api/payments)
CREATE TABLE `payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `booking_id` BIGINT NOT NULL,
    `amount` INT NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL,
    `status` VARCHAR(20) NOT NULL COMMENT 'PAID, FAILED, REFUNDED',
    `ticket_code` VARCHAR(100) NOT NULL,
    `issued_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_booking_id` (`booking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
