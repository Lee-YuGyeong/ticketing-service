package com.yugyeong.ticketing_service.presentation.response.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    JOIN_SUCCESS("회원가입 성공", HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다."),
    ID_PASSWORD_MATCHED("로그인 성공", HttpStatus.OK, "로그인이 성공적으로 완료되었습니다.");

    private final String title;

    private final HttpStatus status;

    private final String detail;
}