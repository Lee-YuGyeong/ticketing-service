package com.yugyeong.ticketing_service.application.service;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Performance;
import com.yugyeong.ticketing_service.domain.entity.PerformanceGrade;
import com.yugyeong.ticketing_service.domain.entity.PerformanceSeat;
import com.yugyeong.ticketing_service.domain.entity.Venue;
import com.yugyeong.ticketing_service.domain.repository.PerformanceRepository;
import com.yugyeong.ticketing_service.domain.repository.SeatRepository;
import com.yugyeong.ticketing_service.domain.repository.VenueRepository;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceUpdateRequestDto;
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
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    /**
     * 전체 공연 목록 조회
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<PerformanceResponseDto> getAllPerformances() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        List<Performance> performances = null;

        //관리자만 삭제된 공연 조회 가능
        if (isAdmin) {
            performances = performanceRepository.findAll();
        } else {
            performances = performanceRepository.findByStatusNot(PerformanceStatus.DELETE);
        }

        return performances.stream()
            .map(performance -> PerformanceResponseDto.builder()
                .name(performance.getName())
                .venue(performance.getVenue())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .description(performance.getDescription())
                .status(performance.getStatus())
                .build())
            .collect(Collectors.toList());
    }

    /**
     * 공연 정보 조회
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public PerformanceResponseDto getPerformance(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Performance performance = null;

        //관리자만 삭제된 공연 조회 가능
        if (isAdmin) {
            performance = performanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));
        } else {
            performance = (Performance) performanceRepository.findByIdAndStatusNot(id,
                    PerformanceStatus.DELETE)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));
        }

        return PerformanceResponseDto.builder()
            .name(performance.getName())
            .venue(performance.getVenue())
            .startDate(performance.getStartDate())
            .endDate(performance.getEndDate())
            .description(performance.getDescription())
            .status(performance.getStatus())
            .build();
    }

    /**
     * 공연 생성
     *
     * @param performanceCreateRequestDto
     */
    public void createPerformance(PerformanceCreateRequestDto performanceCreateRequestDto) {
        // 공연장 조회
        Venue venue = venueRepository.findByIdAndStatus(performanceCreateRequestDto.getVenueId(),
                true)
            .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        List<PerformanceGrade> performanceGradeList = performanceCreateRequestDto.getPerformanceGradeList()
            .stream()
            .map(
                performanceGradeCreateRequestDto -> PerformanceGrade.builder()
                    .name(performanceGradeCreateRequestDto.getName())
                    .price(performanceGradeCreateRequestDto.getPrice())
                    .totalSeats(performanceGradeCreateRequestDto.getTotalSeats())
                    .build()
            ).toList();

        Performance performance = Performance.builder()
            .name(performanceCreateRequestDto.getName())
            .venue(venue)
            .startDate(performanceCreateRequestDto.getStartDate())
            .endDate(performanceCreateRequestDto.getEndDate())
            .description(performanceCreateRequestDto.getDescription())
            .status(PerformanceStatus.ACTIVE)
            .build();

        // 좌석 생성
        int index = 0;
        for (PerformanceGrade performanceGrade : performanceGradeList) {
            for (int i = index + 1; i <= performanceGrade.getTotalSeats() + index; i++) {
                PerformanceSeat performanceSeat = PerformanceSeat.builder()
                    .number(i)
                    .isReserved(false)
                    .performanceGrade(performanceGrade)
                    .build();
                performanceGrade.addPerformanceSeat(performanceSeat);
            }
            performance.addPerformanceGrade(performanceGrade);
            index = index + performanceGrade.getTotalSeats();
        }

        performanceRepository.save(performance);
    }

    /**
     * 공연 수정
     *
     * @param id
     * @param performanceUpdateRequestDto
     */
    public void updatePerformance(Long id,
        PerformanceUpdateRequestDto performanceUpdateRequestDto) {

        // 공연장 유효성 확인 -> and status 수정하기
        Performance performance = (Performance) performanceRepository.findByIdAndStatusNot(id,
                PerformanceStatus.DELETE)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // 이미 예약된 좌석이 있으면 공연 수정 불가
        boolean isReserved = performance.getPerformanceGradeList().stream().anyMatch(
            performanceGrade -> performanceGrade.getTotalSeats()
                != performanceGrade.getRemainSeats());

        if (isReserved) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        // 공연장 조회
        Venue venue = venueRepository.findByIdAndStatus(performanceUpdateRequestDto.getVenueId(),
                true)
            .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        List<PerformanceGrade> performanceGradeList = performanceUpdateRequestDto.getPerformanceGradeList()
            .stream()
            .map(
                performanceGradeUpdateRequestDto -> PerformanceGrade.builder()
                    .name(performanceGradeUpdateRequestDto.getName())
                    .price(performanceGradeUpdateRequestDto.getPrice())
                    .totalSeats(performanceGradeUpdateRequestDto.getTotalSeats())
                    .build()
            ).toList();

        performance.getPerformanceGradeList().clear();

        // 좌석 생성
        int index = 0;
        for (PerformanceGrade performanceGrade : performanceGradeList) {
            for (int i = index + 1; i <= performanceGrade.getTotalSeats() + index; i++) {
                PerformanceSeat performanceSeat = PerformanceSeat.builder()
                    .number(i)
                    .isReserved(false)
                    .performanceGrade(performanceGrade)
                    .build();
                performanceGrade.addPerformanceSeat(performanceSeat);
            }
            performance.addPerformanceGrade(performanceGrade);
            index = index + performanceGrade.getTotalSeats();
        }

        performance.updatePerformance(performanceUpdateRequestDto.getName(),
            performanceUpdateRequestDto.getStartDate(),
            performanceUpdateRequestDto.getEndDate(),
            performanceUpdateRequestDto.getDescription(),
            venue
        );


    }

    /**
     * 공연 삭제
     *
     * @param id
     */
    public void deletePerformance(Long id) {
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        performance.delete();
        performanceRepository.save(performance);
    }

    /**
     * 공연 취소
     *
     * @param id
     */
    public void cancelPerformance(Long id) {
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        performance.cancel();
        performanceRepository.save(performance);
    }

    /**
     * 공연 만료
     *
     * @param id
     */
    public void expirePerformance(Long id) {
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        performance.expire();
        performanceRepository.save(performance);
    }
}
