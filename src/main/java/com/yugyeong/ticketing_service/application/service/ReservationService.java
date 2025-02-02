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
import com.yugyeong.ticketing_service.presentation.dto.reservation.ReservationResponseDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        // TODO: 티켓 개수 줄이기

        Reservation reservation = Reservation.builder()
            .price(reservationCreateRequestDto.getPrice())
            .reservationStatus(ReservationStatus.CONFIRMED)
            .performance(performance)
            .performanceSeat(performanceSeat)
            .user(user)
            .build();

        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getReservations(Long performanceId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Reservation> reservations;

        if (isAdmin(authentication)) {
            reservations = getAdminReservations(performanceId);
        } else if (isManager(authentication)) {
            reservations = getManagerReservations(performanceId);
        } else {
            reservations = getUserReservations(authentication, performanceId);
        }

        return reservations.stream().map(reservation -> ReservationResponseDto.builder()
            .price(reservation.getPrice())
            .reservationStatus(reservation.getReservationStatus())
            .performance(reservation.getPerformance())
            .performanceSeat(reservation.getPerformanceSeat())
            .user(reservation.getUser())
            .build()).collect(Collectors.toList());
    }

    // ADMIN - 전체 조회 가능
    private List<Reservation> getAdminReservations(Long performanceId) {
        return (performanceId == null)
            ? reservationRepository.findAll()
            : reservationRepository.findByPerformanceId(performanceId);
    }

    // MANAGER - 특정 공연의 예약만 조회 가능
    private List<Reservation> getManagerReservations(Long performanceId) {
        if (performanceId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // TODO: 권한이 있는 공연만 조회 가능

        return reservationRepository.findByPerformanceId(performanceId);
    }

    // 일반 사용자 - 본인 예약만 조회 가능
    private List<Reservation> getUserReservations(Authentication authentication,
        Long performanceId) {
        String email = ((PrincipalDetails) authentication.getPrincipal()).getEmail();
        User user = userRepository.findByEmailAndStatus(email, true)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return (performanceId == null)
            ? reservationRepository.findByUserId(user.getId())
            : reservationRepository.findByUserIdAndPerformanceId(user.getId(), performanceId);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private boolean isManager(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"));
    }


}
