package com.yugyeong.ticketing_service.application.service.user;

import com.yugyeong.ticketing_service.domain.Role;
import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.domain.repository.UserRepository;
import com.yugyeong.ticketing_service.presentation.dto.user.UserJoinRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.user.UserResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.user.UserUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(UserJoinRequestDto joinRequestDto) {
        //이메일 중복 체크
        if (userRepository.existsByEmail(joinRequestDto.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = new User(joinRequestDto.email(), joinRequestDto.username(),
            bCryptPasswordEncoder.encode(joinRequestDto.password()), Role.USER);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserResponseDto.builder()
            .email(user.getEmail())
            .username(user.getUsername())
            .build();
    }

    public void updateUser(String email, UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String password = userUpdateRequestDto.password() != null ?
            bCryptPasswordEncoder.encode(userUpdateRequestDto.password()) : null;

        user.updateProfile(userUpdateRequestDto.username(), password);
    }

    public void deactivateUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.deactivate();
        userRepository.save(user);

    }
}
