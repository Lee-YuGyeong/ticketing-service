package com.yugyeong.ticketing_service.domain;

public enum PerformanceStatus {
    ACTIVE,    // 공연이 진행 중
    EXPIRE,   // 공연이 종료됨
    DELETE,   // 공연이 삭제됨
    CANCEL  // 공연이 취소됨
}
