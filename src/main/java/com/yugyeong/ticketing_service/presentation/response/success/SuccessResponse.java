package com.yugyeong.ticketing_service.presentation.response.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "인증 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse {

    @Schema(description = "성공 제목")
    private String title;

    @Schema(description = "HTTP 상태 코드")
    private int status;

    @Schema(description = "성공 설명")
    private final String detail;

    @Schema(description = "응답 데이터")
    private Map<String, Object> data;

}