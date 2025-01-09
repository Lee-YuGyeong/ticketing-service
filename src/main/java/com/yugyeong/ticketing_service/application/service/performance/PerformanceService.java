package com.yugyeong.ticketing_service.application.service.performance;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Performance;
import com.yugyeong.ticketing_service.domain.repository.PerformanceRepository;
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
                .dateTime(performance.getDateTime())
                .description(performance.getDescription())
                .price(performance.getPrice())
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
            .dateTime(performance.getDateTime())
            .description(performance.getDescription())
            .price(performance.getPrice())
            .status(performance.getStatus())
            .build();
    }

    /**
     * 공연 생성
     *
     * @param performanceCreateRequestDto
     */
    public void createPerformance(PerformanceCreateRequestDto performanceCreateRequestDto) {
        Performance performance = Performance.builder()
            .name(performanceCreateRequestDto.getName())
            .venue(performanceCreateRequestDto.getVenue())
            .dateTime(performanceCreateRequestDto.getDateTime())
            .price(performanceCreateRequestDto.getPrice())
            .description(performanceCreateRequestDto.getDescription())
            .status(PerformanceStatus.ACTIVE)
            .seatList(performanceCreateRequestDto.getSeatList())
            .build();

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
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        performance.updatePerformance(performanceUpdateRequestDto.getName(),
            performanceUpdateRequestDto.getVenue(),
            performanceUpdateRequestDto.getDateTime(),
            performanceUpdateRequestDto.getDescription(),
            performanceUpdateRequestDto.getPrice(),
            performanceUpdateRequestDto.getSeatList()
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
