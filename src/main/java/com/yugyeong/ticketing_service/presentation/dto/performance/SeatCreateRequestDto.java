package com.yugyeong.ticketing_service.presentation.dto.performance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeatCreateRequestDto {

    @Schema(description = "좌석 등급", example = "S")
    @NotNull(message = "좌석 등급은 필수 값 입니다.")
    private String grade;

    @Schema(description = "가격", example = "1000.0")
    @NotNull(message = "가격은 필수 값 입니다.")
    @Min(value = 0, message = "가격은 0 이상이여야 합니다.")
    private Double price;

    @Schema(description = "좌석 수", example = "50")
    @NotNull(message = "좌석 수는 필수 값 입니다.")
    @Min(value = 0, message = "좌석 수는 0 이상이여야 합니다.")
    private int count;
}