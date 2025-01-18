package com.yugyeong.ticketing_service.presentation.dto.venue;


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
public class VenueCreateRequestDto {

    @Schema(description = "공연장 이름", example = "테스트 공연장 이름")
    @NotNull(message = "이름은 필수 값 입니다.")
    private String name;

    @Schema(description = "설명", example = "테스트 공연장 입니다.")
    private String description;

    @Schema(description = "총 좌석 수", example = "100")
    private int totalSeats;

}
