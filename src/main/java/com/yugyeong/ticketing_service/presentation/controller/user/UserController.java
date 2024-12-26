package com.yugyeong.ticketing_service.presentation.controller.user;

import com.yugyeong.ticketing_service.application.service.user.UserService;
import com.yugyeong.ticketing_service.presentation.dto.user.UserResponseDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<SuccessResponse> getUserByEmail(@PathVariable("email") String email) {
        UserResponseDto userResponseDto = userService.getUserByEmail(email);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.USER_FOUND.getTitle())
                .status(SuccessCode.USER_FOUND.getStatus().value())
                .detail(SuccessCode.USER_FOUND.getDetail())
                .data(Map.of(
                    "user", userResponseDto
                ))
                .build());
    }

}
