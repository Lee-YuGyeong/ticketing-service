package com.yugyeong.ticketing_service.application.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.Role;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private PerformanceSeatRepository performanceSeatRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void 예약_목록_조회_관리자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.ADMIN));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        List<Reservation> mockReservations = List.of(
            Reservation.builder()
                .price(1000.0)
                .reservationStatus(ReservationStatus.CONFIRMED)
                .performance(mock(Performance.class))
                .performanceSeat(mock(PerformanceSeat.class))
                .user(mock(User.class))
                .build()
        );

        when(reservationRepository.findAll()).thenReturn(mockReservations);

        // when
        List<ReservationResponseDto> reservations = reservationService.getReservations(null);

        // then
        assertThat(reservations).hasSize(1);
        assertThat(reservations).extracting("price").containsExactly(1000.0);
        assertThat(reservations).extracting("reservationStatus")
            .containsExactly(ReservationStatus.CONFIRMED);
    }

    @Test
    void 예약_목록_조회_사용자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        PrincipalDetails mockPrincipal = mock(PrincipalDetails.class);
        doReturn(mockPrincipal).when(authentication).getPrincipal();
        doReturn("user@example.com").when(mockPrincipal).getEmail();

        User mockUser = User.builder()
            .email("user@example.com")
            .build();

        // `id` 필드 강제 설정
        ReflectionTestUtils.setField(mockUser, "id", 100L);

        when(userRepository.findByEmailAndStatus("user@example.com", true))
            .thenReturn(Optional.of(mockUser));

        Long performanceId = 1L;

        List<Reservation> mockReservations = List.of(
            Reservation.builder()
                .price(1500.0)
                .reservationStatus(ReservationStatus.CONFIRMED)
                .performance(mock(Performance.class))
                .performanceSeat(mock(PerformanceSeat.class))
                .user(mockUser)
                .build()
        );

        when(reservationRepository.findByUserIdAndPerformanceId(any(Long.class), any(Long.class)))
            .thenReturn(mockReservations);

        // when
        List<ReservationResponseDto> reservations = reservationService.getReservations(
            performanceId);

        // then
        assertThat(reservations).hasSize(1);
        assertThat(reservations).extracting("price")
            .containsExactly(1500.0);
    }

    @Test
    void 예약_성공() {
        // given
        ReservationCreateRequestDto requestDto = new ReservationCreateRequestDto(1L, 50);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        PrincipalDetails mockPrincipal = mock(PrincipalDetails.class);
        doReturn(mockPrincipal).when(authentication).getPrincipal();
        doReturn("user@example.com").when(mockPrincipal).getEmail();

        User mockUser = User.builder()
            .email("user@example.com")
            .build();
        ReflectionTestUtils.setField(mockUser, "id", 100L);

        when(userRepository.findByEmailAndStatus("user@example.com", true))
            .thenReturn(Optional.of(mockUser));

        Performance mockPerformance = mock(Performance.class);
        when(performanceRepository.findByIdAndStatusNot(1L, PerformanceStatus.DELETE))
            .thenReturn(Optional.of(mockPerformance));

        PerformanceSeat mockSeat = mock(PerformanceSeat.class);
        when(mockSeat.getReserved()).thenReturn(false);
        when(mockSeat.getGrade()).thenReturn("VIP"); // 좌석 등급 추가

        when(performanceSeatRepository.findByNumberAndPerformanceIdAndIsReserved(50, 1L, false))
            .thenReturn(Optional.of(mockSeat));

        PerformanceGrade mockGrade = mock(PerformanceGrade.class);
        when(mockGrade.getName()).thenReturn("VIP"); // PerformanceGrade의 이름을 좌석 등급과 일치시킴
        when(mockGrade.getPrice()).thenReturn(3000.0);

        when(mockPerformance.getPerformanceGradeList()).thenReturn(
            List.of(mockGrade)); // Grade 리스트 추가

        when(mockGrade.getPrice()).thenReturn(5000.0);

        // when
        reservationService.createReservation(requestDto);

        // then
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void 예약_취소_성공() {
        // given
        Long reservationId = 1L;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        PrincipalDetails mockPrincipal = mock(PrincipalDetails.class);
        doReturn(mockPrincipal).when(authentication).getPrincipal();
        doReturn("user@example.com").when(mockPrincipal).getEmail();

        User mockUser = User.builder()
            .email("user@example.com")
            .build();
        ReflectionTestUtils.setField(mockUser, "id", 100L);

        when(userRepository.findByEmailAndStatus("user@example.com", true))
            .thenReturn(Optional.of(mockUser));

        Performance mockPerformance = mock(Performance.class);
        PerformanceSeat mockSeat = mock(PerformanceSeat.class);
        PerformanceGrade mockGrade = mock(PerformanceGrade.class);

        when(mockSeat.getGrade()).thenReturn("VIP");
        when(mockGrade.getName()).thenReturn("VIP");
        when(mockPerformance.getPerformanceGradeList()).thenReturn(List.of(mockGrade));

        Reservation mockReservation = Reservation.builder()
            .price(3000.0)
            .reservationStatus(ReservationStatus.CONFIRMED)
            .performance(mockPerformance)
            .performanceSeat(mockSeat)
            .user(mockUser)
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(
            Optional.of(mockReservation));

        // when
        reservationService.cancelReservation(reservationId);

        // then
        assertThat(mockReservation.getReservationStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(performanceSeatRepository, times(1)).save(any(PerformanceSeat.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}