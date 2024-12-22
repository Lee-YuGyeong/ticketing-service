package com.yugyeong.ticketing_service.presentation.controller.auth;

import com.yugyeong.ticketing_service.application.service.user.UserService;
import com.yugyeong.ticketing_service.presentation.dto.user.JoinRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
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

    @PostMapping("/join")
    public ResponseEntity<SuccessResponse> join(
        @Valid @RequestBody final JoinRequestDto joinRequestDto) {

        userService.join(joinRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.builder()
                .title("회원가입 성공")
                .status(HttpStatus.CREATED.value())
                .detail("회원가입이 성공적으로 완료되었습니다.")
                .build());
    }

}
