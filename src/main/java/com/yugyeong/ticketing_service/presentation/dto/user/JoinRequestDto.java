package com.yugyeong.ticketing_service.presentation.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record JoinRequestDto(
    @NotNull(message = "이메일은 필수 값 입니다.") @Email(message = "유효하지 않은 이메일 형식입니다.") String email,
    @NotNull(message = "이름은 필수 값 입니다.") String username,
    @NotNull(message = "비밀번호는 필수 값 입니다.") @Size(min = 8, message = "비밀번호는 최소 8자리 이상이여야합니다.") String password) {

}
