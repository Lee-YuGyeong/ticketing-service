package com.yugyeong.ticketing_service.infrastructure.persistence.auditor;

import com.yugyeong.ticketing_service.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 객체가 없거나 익명 사용자일 경우
        if (authentication == null || !authentication.isAuthenticated()
            || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("system"); // 기본값으로 "system" 반환
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            // UserDetails 인스턴스로 캐스팅
            UserDetails userDetails = (UserDetails) principal;
            if (userDetails instanceof User) {
                // 실제 User 객체를 얻을 수 있다면 그 이메일을 반환
                return Optional.of(((User) userDetails).getEmail());
            }
        }

        return Optional.empty(); // 유효하지 않은 principal인 경우
    }
}

