package com.yugyeong.ticketing_service.presentation.dto.performance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;


public record PerformanceUpdateRequestDto(
    @Schema(description = "공연 이름")
    @NotNull(message = "이름은 필수 값 입니다.")
    String name,
    @Schema(description = "공연 장소")
    @NotNull(message = "장소는 필수 값 입니다.")
    String venue,
    @Schema(description = "공연 시간")
    @NotNull(message = "시간은 필수 값 입니다.")
    LocalDateTime dateTime,
    @Schema(description = "설명")
    String description,
    @Schema(description = "가격")
    @NotNull(message = "가격은 필수 값 입니다.")
    @Min(value = 0, message = "가격은 0 이상이여야 합니다.") Double price) {

}
