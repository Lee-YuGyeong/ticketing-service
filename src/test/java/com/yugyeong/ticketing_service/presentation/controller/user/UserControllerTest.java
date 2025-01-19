package com.yugyeong.ticketing_service.presentation.controller.user;

import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_EMAIL;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_USERNAME;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugyeong.ticketing_service.application.service.UserService;
import com.yugyeong.ticketing_service.presentation.controller.UserController;
import com.yugyeong.ticketing_service.presentation.dto.user.UserResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.user.UserUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void 사용자_조회_성공() throws Exception {
        // given
        UserResponseDto userResponseDto = UserResponseDto.builder()
            .email(VALID_EMAIL)
            .username(VALID_USERNAME)
            .build();

        when(userService.getUserByEmail(VALID_EMAIL)).thenReturn(userResponseDto);

        // when & then
        mockMvc.perform(get("/user/" + VALID_EMAIL).
                with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.user.email").value(VALID_EMAIL))
            .andExpect(jsonPath("$.data.user.username").value(VALID_USERNAME));

    }

    @Test
    void 사용자_조회_실패() throws Exception {
        //given
        String invalidEmail = "invalid@test.com";
        doThrow(new CustomException(ErrorCode.USER_NOT_FOUND)).when(userService)
            .getUserByEmail(invalidEmail);

        //when & then
        mockMvc.perform(get("/user/" + invalidEmail)
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value(ErrorCode.USER_NOT_FOUND.getTitle()))
            .andExpect(
                jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(ErrorCode.USER_NOT_FOUND.getDetail()));

    }

    @Test
    void 사용자_수정_성공() throws Exception {
        //given
        String newUsername = "New Username";
        String newPassword = "New Password";
        String encodedPassword = "New EncodedPassword";

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(newUsername, newPassword);

        doNothing().when(userService).updateUser(VALID_EMAIL, requestDto);

        //when & then
        mockMvc.perform(patch("/user/" + VALID_EMAIL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(SuccessCode.USER_UPDATE.getTitle()))
            .andExpect(jsonPath("$.status").value(SuccessCode.USER_UPDATE.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.USER_UPDATE.getDetail()));
    }

    @Test
    void 사용자_탈퇴_성공() throws Exception {
        //given
        doNothing().when(userService).deactivateUser(VALID_EMAIL);

        //when & then
        mockMvc.perform(delete("/user/" + VALID_EMAIL).
                with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(SuccessCode.USER_DEACTIVATE.getTitle()))
            .andExpect(jsonPath("$.status").value(SuccessCode.USER_DEACTIVATE.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.USER_DEACTIVATE.getDetail()));
    }

}