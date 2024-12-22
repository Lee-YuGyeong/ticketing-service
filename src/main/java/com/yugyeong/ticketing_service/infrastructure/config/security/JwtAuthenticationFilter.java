package com.yugyeong.ticketing_service.infrastructure.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorResponse;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private final JWTProperties jwtProperties;


    // 로그인 요청 시 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(request.getInputStream(), User.class);

            // 인증을 위한 UsernamePasswordAuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            return authentication;
        } catch (IOException e) {
            throw new AuthenticationException("로그인 요청 처리 중 오류가 발생했습니다.") {
            };
        }
    }

    // 인증이 성공하면 호출되는 메서드 (JWT 토큰 생성 후 응답 반환)
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        SuccessCode successCode = SuccessCode.ID_PASSWORD_MATCHED;

        // 인증된 사용자 정보를 가져옴
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // JWT 토큰 생성
        String jwtToken = JWT.create()
            .withSubject(principalDetails.getUser().getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.EXPIRATION_TIME))
            .withClaim("id", principalDetails.getUser().getId())
            .withClaim("email", principalDetails.getUser().getEmail())
            .sign(Algorithm.HMAC512(jwtProperties.SECRET));

        SuccessResponse successResponse = SuccessResponse.builder()
            .title(successCode.getTitle())
            .status(successCode.getStatus().value())
            .detail(successCode.getDetail())
            .data(Map.of(
                "email", principalDetails.getUser().getEmail(),
                "username", principalDetails.getUser().getUsername()
            ))
            .build();

        response.setHeader("Authorization", jwtProperties.TOKEN_PREFIX + jwtToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(successResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.ID_PASSWORD_NOT_MATCHED;

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
            .type(URI.create(errorCode.getType()))
            .title(errorCode.getTitle())
            .status(errorCode.getStatus().value())
            .detail(errorCode.getDetail())
            .instance(URI.create(request.getRequestURI()))
            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
