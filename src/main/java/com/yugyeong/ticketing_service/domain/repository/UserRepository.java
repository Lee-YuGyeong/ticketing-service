package com.yugyeong.ticketing_service.domain.repository;

import com.yugyeong.ticketing_service.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(@Param("email") String email);

    Optional<User> findByIdAndStatus(Long userId, boolean b);
}
