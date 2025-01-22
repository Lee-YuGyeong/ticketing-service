package com.yugyeong.ticketing_service.testutil;

import com.yugyeong.ticketing_service.domain.Role;
import java.util.List;

public class TestConstants {

    // user 테스트 데이터
    public static final String VALID_EMAIL = "user@example.com";
    public static final String VALID_USERNAME = "user";
    public static final String VALID_PASSWORD = "password123";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final Role ROLE_USER = Role.USER;

    // venue 테스트 데이터
    public static final String VENUE_NAME = "공연장 이름";
    public static final String VENUE_DESCRIPTION = "공연장 설명";
    public static final int VENUE_TOTAL_SEATS = 10;

    // performance 테스트 데이터
    public static final List<String> PERFORMANCE_NAME = List.of("공연 이름 1", "공연 이름 2");
    public static final List<String> PERFORMANCE_DESCRIPTION = List.of("공연 설명 1", "공연 설명 2");

    // grade 테스트 데이터
    public static final List<String> PERFORMANCE_GRADE_NAME = List.of("S", "A");
    public static final List<Double> PERFORMANCE_GRADE_PRICE = List.of(3000.0, 1000.0);
    public static final List<Integer> PERFORMANCE_GRADE_TOTAL_SEATS = List.of(5, 10);

}