package com.yugyeong.ticketing_service.presentation.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreateRequestDto {

    @Schema(description = "공연", example = "1")
    @NotNull(message = "공연은 필수 값 입니다.")
    private Long performanceId;

    @Schema(description = "공연 자리", example = "1")
    @NotNull(message = "공연 자리는 필수 값 입니다.")
    private int performanceSeatNumber;
}
