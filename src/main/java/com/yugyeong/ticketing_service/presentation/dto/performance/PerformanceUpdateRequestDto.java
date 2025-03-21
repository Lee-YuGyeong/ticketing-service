package com.yugyeong.ticketing_service.presentation.dto.performance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceUpdateRequestDto {

    @Schema(description = "공연 이름", example = "테스트 콘서트 이름2")
    @NotNull(message = "이름은 필수 값 입니다.")
    private String name;

    @Schema(description = "공연 장소", example = "1")
    @NotNull(message = "장소는 필수 값 입니다.")
    private Long venueId;

    @Schema(description = "공연 시작 시간", example = "2025-01-10T20:00:00", type = "string")
    @NotNull(message = "시작 시간은 필수 값 입니다.")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "공연 종료 시간", example = "2025-01-17T20:00:00", type = "string")
    @NotNull(message = "종료 시간은 필수 값 입니다.")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(description = "설명", example = "테스트 공연 입니다.")
    private String description;

    @Schema(description = "좌석 등급 리스트", type = "array")
    @NotNull(message = "좌석은 필수 값 입니다.")
    private List<@Valid PerformanceGradeUpdateRequestDto> performanceGradeList;

}
