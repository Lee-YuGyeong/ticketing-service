package com.yugyeong.ticketing_service.presentation.controller.user;

import com.yugyeong.ticketing_service.application.service.user.UserService;
import com.yugyeong.ticketing_service.presentation.dto.user.UserResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.user.UserUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * @param email
     * @return 사용자 정보 조회
     */
    @Operation(
        summary = "사용자 조회",
        description = "사용자의 정보를 반환합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
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

    /**
     * @param email
     * @return 사용자 정보 수정
     */
    @Operation(
        summary = "사용자 수정",
        description = "사용자의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 수정 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PatchMapping("/{email}")
    public ResponseEntity<SuccessResponse> updateUser(@PathVariable("email") String email,
        @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {
        userService.updateUser(email, userUpdateRequestDto);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.USER_UPDATE.getTitle())
                .status(SuccessCode.USER_UPDATE.getStatus().value())
                .detail(SuccessCode.USER_UPDATE.getDetail())
                .build());
    }

    /**
     * @param email
     * @return 사용자 탈퇴
     */
    @Operation(
        summary = "사용자 탈퇴",
        description = "사용자 탈퇴 처리를 합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 탈퇴 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @DeleteMapping("/{email}")
    public ResponseEntity<SuccessResponse> deactivateUser(@PathVariable("email") String email) {
        userService.deactivateUser(email);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.USER_DEACTIVATE.getTitle())
                .status(SuccessCode.USER_DEACTIVATE.getStatus().value())
                .detail(SuccessCode.USER_DEACTIVATE.getDetail())
                .build());
    }


}
