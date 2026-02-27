package com.ticket.core.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE("C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED("C002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR("C003", "서버 내부 오류가 발생했습니다."),
    INVALID_TYPE_VALUE("C004", "잘못된 타입의 요청입니다."),
    MISSING_REQUEST_PARAMETER("C005", "필수 파라미터가 누락되었습니다."),
    MISSING_REQUEST_HEADER("C006", "필수 헤더가 누락되었습니다."),
    RESOURCE_NOT_FOUND("C007", "요청하신 경로를 찾을 수 없습니다."),

    // Queue
    QUEUE_TOKEN_NOT_FOUND("Q001", "대기열 토큰을 찾을 수 없습니다."),
    QUEUE_TOKEN_EXPIRED("Q002", "만료된 대기열 토큰입니다."),
    NOT_ACTIVE_QUEUE("Q003", "아직 접근 순서가 되지 않았습니다."),

    // Concert & Seat
    CONCERT_NOT_FOUND("T001", "콘서트를 찾을 수 없습니다."),
    CONCERT_DATE_NOT_FOUND("T002", "콘서트 일정을 찾을 수 없습니다."),
    SEAT_NOT_FOUND("T003", "좌석을 찾을 수 없습니다."),
    SEAT_ALREADY_BOOKED("T004", "이미 예약된 좌석입니다."),

    // Booking & Payment
    BOOKING_NOT_FOUND("B001", "예약 내역을 찾을 수 없습니다."),
    PAYMENT_FAILED("P001", "결제 처리에 실패했습니다.");

    private final String code;
    private final String message;
}
