package com.yugyeong.ticketing_service.application.service;

import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.domain.repository.UserRepository;
import com.yugyeong.ticketing_service.infrastructure.config.security.PrincipalDetails;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws CustomException {
        User userEntity = userRepository.findByEmail(email)
            .orElseThrow(
                () -> new CustomException(ErrorCode.ID_PASSWORD_NOT_MATCHED));
        return new PrincipalDetails(userEntity);
    }
}