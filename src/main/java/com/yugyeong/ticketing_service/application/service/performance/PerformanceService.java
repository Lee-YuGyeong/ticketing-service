package com.yugyeong.ticketing_service.application.service.performance;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private PerformanceRepository performanceRepository;

    @Transactional(readOnly = true)
    public List<PerformanceResponseDto> getAllPerformances() {
        List<Performance> performances = performanceRepository.findAll();

        return performances.stream()
            .map(performance -> PerformanceResponseDto.builder()
                .name(performance.getName())
                .venue(performance.getVenue())
                .dateTime(performance.getDateTime())
                .description(performance.getDescription())
                .price(performance.getPrice())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PerformanceResponseDto getPerformance(Long id) {
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        return PerformanceResponseDto.builder()
            .name(performance.getName())
            .venue(performance.getVenue())
            .dateTime(performance.getDateTime())
            .description(performance.getDescription())
            .price(performance.getPrice())
            .build();
    }

    public void createPerformance(PerformanceCreateRequestDto performanceCreateRequestDto) {
        Performance performance = Performance.builder()
            .name(performanceCreateRequestDto.name())
            .venue(performanceCreateRequestDto.venue())
            .dateTime(performanceCreateRequestDto.dateTime())
            .price(performanceCreateRequestDto.price())
            .description(performanceCreateRequestDto.description())
            .build();

        performanceRepository.save(performance);
    }

    public void updatePerformance(Long id,
        PerformanceUpdateRequestDto performanceUpdateRequestDto) {
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        performance.updatePerformance(performance.getName(),
            performance.getVenue(),
            performance.getDateTime(),
            performance.getDescription(),
            performance.getPrice()
        );

    }
}
