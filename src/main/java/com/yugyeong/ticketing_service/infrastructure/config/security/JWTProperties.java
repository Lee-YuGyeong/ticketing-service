package com.yugyeong.ticketing_service.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTProperties {

    @Value("${jwt.secret}")
    public String SECRET;   // 비밀 값
    @Value("${jwt.expiration-time}")
    public int EXPIRATION_TIME;  // 토큰 만료 시간

    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX;  // 토큰 접두사

    @Value("${jwt.header-string}")
    public String HEADER_STRING;  // 헤더 키
}
