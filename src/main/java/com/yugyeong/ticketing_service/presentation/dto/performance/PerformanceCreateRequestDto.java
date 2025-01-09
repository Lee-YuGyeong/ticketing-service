package com.yugyeong.ticketing_service.presentation.dto.performance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yugyeong.ticketing_service.domain.entity.Seat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
public class PerformanceCreateRequestDto {

    @Schema(description = "공연 이름", example = "테스트 콘서트 이름")
    @NotNull(message = "이름은 필수 값 입니다.")
    private String name;

    @Schema(description = "공연 장소", example = "테스트 공연 장소")
    @NotNull(message = "장소는 필수 값 입니다.")
    private String venue;

    @Schema(description = "공연 시간", example = "2025-01-10T20:00:00", type = "string")
    @NotNull(message = "시간은 필수 값 입니다.")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTime;

    @Schema(description = "설명", example = "테스트 공연 입니다.")
    private String description;

    @Schema(description = "가격", example = "1000.0")
    @NotNull(message = "가격은 필수 값 입니다.")
    @Min(value = 0, message = "가격은 0 이상이여야 합니다.")
    private Double price;

    @ArraySchema(
        schema = @Schema(
            description = "좌석",
            implementation = Seat.class,
            example = "[{\"grade\": \"S\", \"price\": 1000.0, \"count\": 50}, {\"grade\": \"A\", \"price\": 500.0, \"count\": 100}]"
        )
    )
    @NotNull(message = "좌석은 필수 값 입니다.")
    private List<Seat> seatList;

}
