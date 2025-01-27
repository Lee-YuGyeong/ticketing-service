package com.yugyeong.ticketing_service.presentation.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    CLIENT_SYSTEM_NOT_FOUND("/errors/not-found", "DATA NOT FOUND", HttpStatus.NOT_FOUND,
        "존재하지 않는 클라이언트 시스템 입니다."),
    JWT_NOT_VALID("/errors/authentication", "AUTH NOT VALID", HttpStatus.UNAUTHORIZED,
        "JWT 가 유효하지 않습니다."),
    ID_PASSWORD_NOT_MATCHED("/errors/authentication", "AUTH NOT VALID", HttpStatus.UNAUTHORIZED,
        "아이디 또는 비밀번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXISTS("/errors/email-already-exists", "EMAIL ALREADY EXIST", HttpStatus.CONFLICT
        , "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND("/errors/user-not-found", "USER NOT FOUND", HttpStatus.NOT_FOUND,
        "사용자가 존재하지 않습니다."),
    USER_ALREADY_DEACTIVATE("/errors/user-already-deactivate", "USER ALREADY DEACTIVATE",
        HttpStatus.CONFLICT,
        "이미 탈퇴한 사용자입니다."),
    PERFORMANCE_NOT_FOUND("/errors/performance-not-found", "PERFORMANCE NOT FOUND",
        HttpStatus.NOT_FOUND,
        "공연이 존재하지 않습니다."),
    PERFORMANCE_ALREADY_DELETED("/errors/performance-already-deleted",
        "PERFORMANCE ALREADY DELETED",
        HttpStatus.CONFLICT,
        "이미 삭제된 공연장입니다."),
    PERFORMANCE_ALREADY_CANCELLED("/errors/performance-already-cancelled",
        "PERFORMANCE ALREADY CANCELLED",
        HttpStatus.CONFLICT,
        "이미 공연이 취소되었습니다."),
    PERFORMANCE_ALREADY_EXPIRED("/errors/performance-already-expired",
        "PERFORMANCE ALREADY EXPIRED",
        HttpStatus.CONFLICT,
        "이미 공연이 만료되었습니다."),
    SEAT_ALREADY_RESERVED("/errors/seat-already-reserved", "SEAT ALREADY RESERVED",
        HttpStatus.CONFLICT,
        "좌석이 이미 예매되었습니다."),
    VENUE_NOT_FOUND("/errors/venue-not-found", "VENUE NOT FOUND",
        HttpStatus.NOT_FOUND,
        "공연장이 존재하지 않습니다."),
    PERFORMANCE_SEAT_ALREADY_RESERVE("/errors/performance-seat-already-reserve",
        "PERFORMANCE SEAT ALREADY RESERVE",
        HttpStatus.CONFLICT,
        "이미 좌석이 예약되었습니다.");


    private final String type;

    private final String title;

    private final HttpStatus status;

    private final String detail;
}