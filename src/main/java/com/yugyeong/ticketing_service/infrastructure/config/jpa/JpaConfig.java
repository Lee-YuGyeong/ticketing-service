package com.yugyeong.ticketing_service.infrastructure.config.jpa;

import com.yugyeong.ticketing_service.infrastructure.persistence.auditor.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}