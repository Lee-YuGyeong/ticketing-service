package com.yugyeong.ticketing_service.presentation.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponseDto {

    private String email;
    private String username;

}
