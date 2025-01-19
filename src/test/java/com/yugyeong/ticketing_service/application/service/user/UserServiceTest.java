package com.yugyeong.ticketing_service.application.service.user;

import static com.yugyeong.ticketing_service.testutil.TestConstants.ENCODED_PASSWORD;
import static com.yugyeong.ticketing_service.testutil.TestConstants.ROLE_USER;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_EMAIL;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_PASSWORD;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_USERNAME;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yugyeong.ticketing_service.application.service.PrincipalDetailsService;
import com.yugyeong.ticketing_service.application.service.UserService;
import com.yugyeong.ticketing_service.domain.Role;
import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.domain.repository.UserRepository;
import com.yugyeong.ticketing_service.infrastructure.config.security.PrincipalDetails;
import com.yugyeong.ticketing_service.presentation.dto.user.UserJoinRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.user.UserResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.user.UserUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private UserService userService;

    @InjectMocks
    private PrincipalDetailsService principalDetailsService;

    @Test
    void 회원가입_성공() {
        //given
        UserJoinRequestDto joinRequestDto = new UserJoinRequestDto(VALID_EMAIL, VALID_USERNAME,
            VALID_PASSWORD, ROLE_USER);

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
        UserJoinRequestDto joinRequestDto = new UserJoinRequestDto(VALID_EMAIL, VALID_USERNAME,
            VALID_PASSWORD, ROLE_USER);

        when(userRepository.existsByEmail(joinRequestDto.email())).thenReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.join(joinRequestDto));

        //then
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 로그인_성공() {
        //given
        User user = new User(VALID_EMAIL, VALID_USERNAME, VALID_PASSWORD, Role.USER);

        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));

        //when
        UserDetails userDetails = principalDetailsService.loadUserByUsername(VALID_EMAIL);

        //then
        PrincipalDetails principalDetails = (PrincipalDetails) userDetails;
        assertNotNull(principalDetails);
        assertEquals(VALID_EMAIL, principalDetails.getEmail());
        assertEquals(VALID_USERNAME, principalDetails.getUsername());

    }

    @Test
    void 로그인_실패_존재하지_않는_이메일() {
        //given
        String invalidEmail = "invalid@test.com";

        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, ()
            -> principalDetailsService.loadUserByUsername(invalidEmail));

        //then
        assertEquals(ErrorCode.ID_PASSWORD_NOT_MATCHED.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.ID_PASSWORD_NOT_MATCHED, exception.getErrorCode());
    }

    @Test
    void 사용자_조회_성공() {
        //given
        User user = new User(VALID_EMAIL, VALID_USERNAME, VALID_PASSWORD, Role.USER);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));

        //when
        UserResponseDto userResponseDto = userService.getUserByEmail(VALID_EMAIL);

        //then
        assertNotNull(userResponseDto);
        assertEquals(VALID_EMAIL, userResponseDto.getEmail());
        assertEquals(VALID_USERNAME, userResponseDto.getUsername());

    }

    @Test
    void 사용자_조회_실패() {
        //given
        String invalidEmail = "invalid@test.com";
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.getUserByEmail(invalidEmail));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void 사용자_수정_성공() {
        //given
        String newUsername = "New Username";
        String newPassword = "New Password";
        String encodedPassword = "New EncodedPassword";

        User user = new User(VALID_EMAIL, VALID_USERNAME, ENCODED_PASSWORD, Role.USER);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(newUsername, newPassword);

        //when
        userService.updateUser(VALID_EMAIL, requestDto);

        //then
        assertThat(user.getUsername()).isEqualTo(newUsername);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        verify(userRepository, times(1)).findByEmail(VALID_EMAIL);
        verify(bCryptPasswordEncoder, times(1)).encode(newPassword);
    }

    @Test
    void 사용자_수정_실패_사용자_없음() {
        //given
        String newUsername = "New Username";
        String newPassword = "New Password";
        String invalidEmail = "invalid@test.com";

        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(newUsername, newPassword);

        //when
        CustomException exception = assertThrows(CustomException.class, ()
            -> userService.updateUser(invalidEmail, requestDto));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void 사용자_탈퇴_성공() {
        //given
        User user = new User(VALID_EMAIL, VALID_USERNAME, VALID_PASSWORD, Role.USER);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));

        //when
        userService.deactivateUser(VALID_EMAIL);

        //then
        assertEquals(user.getStatus(), false);
        verify(userRepository).save(user);
    }

    @Test
    void 사용자_탈퇴_실패_사용자_없음() {
        //given
        String invalidEmail = "invalid@test.com";
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.deactivateUser(invalidEmail));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void 사용자_탈퇴_실패_이미_탈퇴됨() {
        //given
        User user = new User(VALID_EMAIL, VALID_USERNAME, VALID_PASSWORD, Role.USER);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));
        user.deactivate();

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.deactivateUser(VALID_EMAIL));

        //then
        assertEquals(ErrorCode.USER_ALREADY_DEACTIVATE, exception.getErrorCode());
        verify(userRepository, never()).save(any());
    }


}