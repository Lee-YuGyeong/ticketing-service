package com.yugyeong.ticketing_service.domain.repository;

import static com.yugyeong.ticketing_service.testutil.TestConstants.ENCODED_PASSWORD;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_EMAIL;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VALID_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yugyeong.ticketing_service.domain.Role;
import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.infrastructure.config.jpa.JpaConfig;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail() {
        //given
        User user = new User(VALID_EMAIL, VALID_USERNAME, ENCODED_PASSWORD, Role.USER);
        userRepository.save(user);

        //when
        Optional<User> result = userRepository.findByEmail(VALID_EMAIL);

        //then
        assertTrue(result.isPresent());
        assertEquals(VALID_EMAIL, result.get().getEmail());
    }

    @Test
    void existsByEmail() {
        //given
        User user = new User(VALID_EMAIL, VALID_USERNAME, ENCODED_PASSWORD, Role.USER);
        userRepository.save(user);

        //when
        boolean result = userRepository.existsByEmail(VALID_EMAIL);

        //then
        assertTrue(result);
    }
}