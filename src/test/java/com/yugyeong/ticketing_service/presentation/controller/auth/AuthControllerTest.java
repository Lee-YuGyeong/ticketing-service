package com.yugyeong.ticketing_service.presentation.controller.auth;

import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_EMAIL;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_PASSWORD;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_USERNAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugyeong.ticketing_service.application.service.user.UserService;
import com.yugyeong.ticketing_service.presentation.dto.user.JoinRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 회원가입_성공() throws Exception {
        //given
        JoinRequestDto joinRequestDto = new JoinRequestDto(VALID_EMAIL, VALID_USERNAME,
            VALID_PASSWORD);
        doNothing().when(userService).join(any(JoinRequestDto.class));

        //when & then
        mockMvc.perform(post("/auth/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequestDto))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(SuccessCode.JOIN_SUCCESS.getTitle()))
            .andExpect(jsonPath("$.status").value(SuccessCode.JOIN_SUCCESS.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.JOIN_SUCCESS.getDetail()));

    }
}