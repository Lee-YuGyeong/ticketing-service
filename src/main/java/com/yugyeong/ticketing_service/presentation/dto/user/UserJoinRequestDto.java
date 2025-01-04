package com.yugyeong.ticketing_service.presentation.dto.user;

import com.yugyeong.ticketing_service.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UserJoinRequestDto(
    @Schema(description = "사용자 이메일")
    @NotNull(message = "이메일은 필수 값 입니다.") @Email(message = "유효하지 않은 이메일 형식입니다.") String email,
    @Schema(description = "사용자 이름")
    @NotNull(message = "이름은 필수 값 입니다.") String username,
    @Schema(description = "사용자 비밀번호")
    @NotNull(message = "비밀번호는 필수 값 입니다.") @Size(min = 8, message = "비밀번호는 최소 8자리 이상이여야합니다.") String password,
    @Schema(description = "권한")
    @NotNull(message = "권한은 필수 값 입니다.") Role role) {

}
