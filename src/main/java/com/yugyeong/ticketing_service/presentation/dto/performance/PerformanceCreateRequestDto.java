package com.yugyeong.ticketing_service.presentation.dto.performance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceCreateRequestDto {

    @Schema(description = "공연 이름")
    @NotNull(message = "이름은 필수 값 입니다.")
    String name;

    @Schema(description = "공연 장소")
    @NotNull(message = "장소는 필수 값 입니다.")
    String venue;

    @Schema(description = "공연 시간")
    @NotNull(message = "시간은 필수 값 입니다.")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime dateTime;

    @Schema(description = "설명")
    String description;

    @Schema(description = "가격")
    @NotNull(message = "가격은 필수 값 입니다.")
    @Min(value = 0, message = "가격은 0 이상이여야 합니다.") Double price;
}
