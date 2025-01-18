package com.yugyeong.ticketing_service.presentation.dto.performance;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Venue;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PerformanceResponseDto {

    private String name; // 공연 이름
    private Venue venue; // 공연 장소
    private LocalDateTime startDate; // 공연 시작 일시
    private LocalDateTime endDate; // 공연 종료 일시
    private String description; // 공연 설명
    private Double price; // 가격
    private PerformanceStatus status; //공연 상태
}
