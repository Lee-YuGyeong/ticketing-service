package com.yugyeong.ticketing_service.application.service;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Performance;
import com.yugyeong.ticketing_service.domain.entity.PerformanceSeat;
import com.yugyeong.ticketing_service.domain.entity.Reservation;
import com.yugyeong.ticketing_service.domain.entity.ReservationStatus;
import com.yugyeong.ticketing_service.domain.entity.User;
import com.yugyeong.ticketing_service.domain.repository.PerformanceRepository;
import com.yugyeong.ticketing_service.domain.repository.PerformanceSeatRepository;
import com.yugyeong.ticketing_service.domain.repository.ReservationRepository;
import com.yugyeong.ticketing_service.domain.repository.UserRepository;
import com.yugyeong.ticketing_service.infrastructure.config.security.PrincipalDetails;
import com.yugyeong.ticketing_service.presentation.dto.reservation.ReservationCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PerformanceRepository performanceRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final UserRepository userRepository;

    public void createReservation(ReservationCreateRequestDto reservationCreateRequestDto) {
        // 공연 조회
        Performance performance = performanceRepository.findByIdAndStatusNot(
                reservationCreateRequestDto.getPerformanceId(),
                PerformanceStatus.DELETE)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // 좌석 조회
        PerformanceSeat performanceSeat = performanceSeatRepository.findByNumberAndPerformanceIdAndIsReserved(
                reservationCreateRequestDto.getPerformanceSeatNumber(),
                reservationCreateRequestDto.getPerformanceId(), false)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_SEAT_ALREADY_RESERVE));

        // 로그인 유저 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((PrincipalDetails) authentication.getPrincipal()).getEmail();

        User user = userRepository.findByEmailAndStatus(email, true)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = Reservation.builder()
            .price(reservationCreateRequestDto.getPrice())
            .reservationStatus(ReservationStatus.CONFIRMED)
            .performance(performance)
            .performanceSeat(performanceSeat)
            .user(user)
            .build();

        reservationRepository.save(reservation);
    }
}
