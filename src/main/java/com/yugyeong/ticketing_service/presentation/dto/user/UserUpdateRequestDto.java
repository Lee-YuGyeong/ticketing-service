package com.yugyeong.ticketing_service.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDto(
    @Schema(description = "사용자 이름", example = "테스트 사용자 이름")
    String username,
    @Schema(description = "사용자 비밀번호", example = "test123456789")
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이여야합니다.") String password) {

}
