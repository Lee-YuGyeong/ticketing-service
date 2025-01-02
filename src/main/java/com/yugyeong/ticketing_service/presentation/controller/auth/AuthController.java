package com.yugyeong.ticketing_service.presentation.controller.auth;

import com.yugyeong.ticketing_service.application.service.user.UserService;
import com.yugyeong.ticketing_service.presentation.dto.user.UserJoinRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Operation(
        summary = "사용자 회원가입",
        description = "사용자 회원가입을 진행합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "사용자 회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @PostMapping("/join")
    public ResponseEntity<SuccessResponse> join(
        @Valid @RequestBody final UserJoinRequestDto joinRequestDto) {

        userService.join(joinRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.builder()
                .title(SuccessCode.JOIN_SUCCESS.getTitle())
                .status(SuccessCode.JOIN_SUCCESS.getStatus().value())
                .detail(SuccessCode.JOIN_SUCCESS.getDetail())
                .build());
    }

}
