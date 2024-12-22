package com.yugyeong.ticketing_service.presentation.dto.user;

import jakarta.validation.constraints.NotNull;


public record LoginRequestDto(
    @NotNull(message = "이메일은 필수 값 입니다.") String email,
    @NotNull(message = "비밀번호는 필수 값 입니다.") String password) {

}
