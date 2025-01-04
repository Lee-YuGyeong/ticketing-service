package com.yugyeong.ticketing_service.presentation.dto.performance;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PerformanceResponseDto {

    private String name; // 공연 이름
    private String venue; // 공연 장소
    private LocalDateTime dateTime; // 공연 일시
    private String description; // 공연 설명
    private Double price; // 가격
}
