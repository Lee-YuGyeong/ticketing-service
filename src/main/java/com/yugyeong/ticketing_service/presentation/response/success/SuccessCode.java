package com.yugyeong.ticketing_service.presentation.response.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    JOIN_SUCCESS("회원가입 성공", HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다."),
    ID_PASSWORD_MATCHED("로그인 성공", HttpStatus.OK, "로그인이 성공적으로 완료되었습니다."),
    USER_FOUND("사용자 조회 성공", HttpStatus.OK, "사용자 조회가 성공적으로 완료되었습니다."),
    USER_DEACTIVATE("사용자 탈퇴 성공", HttpStatus.OK, "사용자 탈퇴가 성공적으로 완료되었습니다."),
    USER_UPDATE("사용자 정보 수정 성공", HttpStatus.OK, "사용자 정보 수정이 성공적으로 완료되었습니다."),
    PERFORMANCE_FOUND("공연장 정보 조회 성공", HttpStatus.OK, "공연장 정보 조회가 성공적으로 완료되었습니다."),
    PERFORMANCE_CREATE("공연장 등록 성공", HttpStatus.CREATED, "공연장 등록이 성공적으로 완료되었습니다."),
    PERFORMANCE_UPDATE("공연장 수정 성공", HttpStatus.CREATED, "공연장 수정이 성공적으로 완료되었습니다.");

    private final String title;

    private final HttpStatus status;

    private final String detail;
}