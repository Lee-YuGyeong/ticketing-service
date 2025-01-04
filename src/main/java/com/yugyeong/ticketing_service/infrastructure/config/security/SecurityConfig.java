package com.yugyeong.ticketing_service.infrastructure.config.security;


import com.yugyeong.ticketing_service.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final JWTProperties jwtProperties;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
            authenticationManager,
            jwtProperties); // // 사용자의 JWT 토큰을 검증하고 인증을 수행하는 JwtAuthenticationFilter 생성
        jwtAuthenticationFilter.setFilterProcessesUrl(
            "/auth/login"); // JWT 인증 필터가 인증을 수행할 엔드포인트 설정

        http.csrf(CsrfConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilter(corsFilter)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .addFilter(jwtAuthenticationFilter)
            .addFilter(
                new JwtAuthorizationFilter(authenticationManager, userRepository, jwtProperties))
            .addFilterBefore(
                new JwtAuthorizationFilter(authenticationManager, userRepository, jwtProperties),
                UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            );

        return http.build();
    }

}
