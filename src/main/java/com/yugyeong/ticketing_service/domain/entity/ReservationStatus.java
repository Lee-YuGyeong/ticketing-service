package com.yugyeong.ticketing_service.domain.entity;

public enum ReservationStatus {
    INIT, // 초기 상태
    CONFIRMED, // 예약 완료 상태
    CANCELLED // 예약 취소 상태
}
