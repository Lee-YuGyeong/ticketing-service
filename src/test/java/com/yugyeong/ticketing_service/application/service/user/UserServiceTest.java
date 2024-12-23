package com.yugyeong.ticketing_service.application.service.user;

import static com.yugyeong.ticketing_service.testutil.TestConstants.ENCODED_PASSWORD;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_EMAIL;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_PASSWORD;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_USERNAME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yugyeong.ticketing_service.domain.Role;
import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.domain.repository.UserRepository;
import com.yugyeong.ticketing_service.presentation.dto.user.JoinRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void 회원가입_성공() {
        //given
        JoinRequestDto joinRequestDto = new JoinRequestDto(VALID_EMAIL, VALID_USERNAME,
            VALID_PASSWORD);

        when(userRepository.existsByEmail(joinRequestDto.email())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(joinRequestDto.password())).thenReturn(ENCODED_PASSWORD);

        //when
        userService.join(joinRequestDto);

        //then
        verify(userRepository, times(1))
            .save(argThat(user -> {
                assertAll("User",
                    () -> assertEquals(VALID_EMAIL, user.getEmail()),
                    () -> assertEquals(VALID_USERNAME, user.getUsername()),
                    () -> assertEquals(ENCODED_PASSWORD, user.getPassword()),
                    () -> assertEquals(Role.USER, user.getRole())
                );
                return true;
            }));
    }

    @Test
    void 회원가입_실패_이메일_중복() {
        //given
        JoinRequestDto joinRequestDto = new JoinRequestDto(VALID_EMAIL, VALID_USERNAME,
            VALID_PASSWORD);

        when(userRepository.existsByEmail(joinRequestDto.email())).thenReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.join(joinRequestDto));

        //then
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());

        verify(userRepository, never()).save(any(User.class));
    }

}