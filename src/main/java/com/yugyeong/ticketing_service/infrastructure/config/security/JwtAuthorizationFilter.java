package com.yugyeong.ticketing_service.infrastructure.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.domain.repository.UserRepository;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final JWTProperties jwtProperties;

    public JwtAuthorizationFilter(
        AuthenticationManager authenticationManager, UserRepository userRepository,
        JWTProperties jwtProperties) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(jwtProperties.HEADER_STRING);
        String uri = request.getRequestURI();

        // 로그인, 회원가입 요청은 필터를 거치지 않도록 설정
        if (uri.startsWith("/auth/") ||
            uri.startsWith("/v3/api-docs") ||
            uri.startsWith("/swagger-ui")) {
            chain.doFilter(request, response);
            return;
        }

        // header가 없거나 유효한 형식이 아닐 경우
        if (header == null || !header.startsWith(jwtProperties.TOKEN_PREFIX)) {
            sendErrorResponse(ErrorCode.JWT_NOT_VALID, response, request);
            return;
        }

        String token = header.replace(jwtProperties.TOKEN_PREFIX, "");
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtProperties.SECRET))
                .build()
                .verify(token);

            String email = decodedJWT.getClaim("email").asString();

            if (email == null || email.trim().isEmpty()) {
                sendErrorResponse(ErrorCode.JWT_NOT_VALID, response, request);
                return;
            }

            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                sendErrorResponse(ErrorCode.JWT_NOT_VALID, response, request);
                return;
            }

            User user = userOptional.get();

            PrincipalDetails principalDetails = new PrincipalDetails(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (JWTDecodeException e) {
            sendErrorResponse(ErrorCode.JWT_NOT_VALID, response, request);
        }

        chain.doFilter(request, response);
    }

    private static void sendErrorResponse(ErrorCode error, HttpServletResponse response,
        HttpServletRequest request) throws IOException {

        // 응답 상태 설정
        response.setStatus(error.getStatus().value());
        response.setCharacterEncoding("UTF-8");

        // ErrorResponse 객체 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
            .type(URI.create(error.getType()))
            .title(error.getTitle())
            .status(error.getStatus().value())
            .detail(error.getDetail())
            .instance(URI.create(request.getRequestURI()))
            .build();

        // ObjectMapper로 JSON 응답 작성
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
