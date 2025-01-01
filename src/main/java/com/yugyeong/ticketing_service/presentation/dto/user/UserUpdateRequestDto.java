package com.yugyeong.ticketing_service.presentation.dto.user;

import jakarta.validation.constraints.Size;

public record UserUpdateRequestDto(
    String username,
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이여야합니다.") String password) {

}
