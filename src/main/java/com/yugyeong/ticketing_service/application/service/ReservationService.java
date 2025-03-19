package com.yugyeong.ticketing_service.application.service;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Performance;
import com.yugyeong.ticketing_service.domain.entity.PerformanceGrade;
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

        // 좌석이 이미 예약되었는지 다시 한 번 확인 (동시성 방지)
        if (performanceSeat.getReserved()) {
            throw new CustomException(ErrorCode.PERFORMANCE_SEAT_ALREADY_RESERVE);
        }

        // 좌석을 예약된 상태로 변경
        performanceSeat.setReserved(true);
        performanceSeatRepository.save(performanceSeat); // 변경 사항 저장 (필요할 경우)

        // 로그인 유저 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((PrincipalDetails) authentication.getPrincipal()).getEmail();

        User user = userRepository.findByEmailAndStatus(email, true)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 해당 좌석이 속한 PerformanceGrade 찾기
        PerformanceGrade performanceGrade = performance.getPerformanceGradeList().stream()
            .filter(pg -> pg.getName().equals(performanceSeat.getGrade()))  // 좌석 등급 매칭
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_GRADE_NOT_FOUND));

        // 티켓 개수 줄이기 (PerformanceGrade 내 예약 처리)
        performanceGrade.reservePerformance();

        Reservation reservation = Reservation.builder()
            .price(performanceGrade.getPrice())
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

        // 현재 로그인한 매니저 이메일 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);  // 인증되지 않은 사용자는 접근 불가
        }

        String email = ((PrincipalDetails) authentication.getPrincipal()).getEmail();

        // 매니저가 만든 공연 리스트 조회 (null 방지)
        List<Long> managedPerformanceIds = performanceRepository.findPerformanceIdsByManagerEmail(
            email);
        if (managedPerformanceIds == null) {
            managedPerformanceIds = List.of();  // null 방지를 위해 빈 리스트로 초기화
        }

        // 🔍 디버깅 로그 추가 (테스트 시 확인 가능)
        System.out.println("🔍 매니저 이메일: " + email);
        System.out.println("🎭 매니저가 관리하는 공연 ID 목록: " + managedPerformanceIds);
        System.out.println("🎟 요청된 공연 ID: " + performanceId);

        // 매니저가 만든 공연이 아닌 경우 예외 발생
        if (!managedPerformanceIds.contains(performanceId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

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
