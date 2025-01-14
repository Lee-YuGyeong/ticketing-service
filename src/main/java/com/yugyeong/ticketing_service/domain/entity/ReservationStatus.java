package com.yugyeong.ticketing_service.domain.entity;

public enum ReservationStatus {
    INIT, // 초기 상태
    PENDING, // 예약 완료, 결재 대기 상태
    CONFIRMED // 결재 완료 상태
}
