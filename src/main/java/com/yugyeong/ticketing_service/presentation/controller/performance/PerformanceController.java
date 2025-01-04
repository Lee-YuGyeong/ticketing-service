package com.yugyeong.ticketing_service.presentation.controller.performance;

import com.yugyeong.ticketing_service.application.service.performance.PerformanceService;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performance")
public class PerformanceController {

    private PerformanceService performanceService;

    @Operation(
        summary = "공연장 조회",
        description = "공연장 목록 정보를 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "공연장 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @GetMapping("/performances")
    public ResponseEntity<SuccessResponse> getAllPerformances() {
        List<PerformanceResponseDto> performances = performanceService.getAllPerformances();

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_FOUND.getTitle())
                .status(SuccessCode.PERFORMANCE_FOUND.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_FOUND.getDetail())
                .data(Map.of(
                    "performances", performances
                ))
                .build());
    }

    @Operation(
        summary = "공연장 조회",
        description = "공연장의 정보를 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "공연장 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getPerformance(@PathVariable("id") Long id) {
        PerformanceResponseDto performanceResponseDto = performanceService.getPerformance(id);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_FOUND.getTitle())
                .status(SuccessCode.PERFORMANCE_FOUND.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_FOUND.getDetail())
                .data(Map.of(
                    "performance", performanceResponseDto
                ))
                .build());
    }

    @Operation(
        summary = "공연장 등록",
        description = "공연장을 등록합니다.",
        responses = {
            @ApiResponse(responseCode = "201", description = "공연장 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
        }
    )
    @PostMapping
    public ResponseEntity<SuccessResponse> createPerformance(@RequestBody
    PerformanceCreateRequestDto performanceCreateRequestDto) {
        performanceService.createPerformance(performanceCreateRequestDto);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_CREATE.getTitle())
                .status(SuccessCode.PERFORMANCE_CREATE.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_CREATE.getDetail())
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse> updatePerformance(@PathVariable("id") Long id,
        @RequestBody PerformanceUpdateRequestDto performanceUpdateRequestDto) {
        performanceService.updatePerformance(id, performanceUpdateRequestDto);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_UPDATE.getTitle())
                .status(SuccessCode.PERFORMANCE_UPDATE.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_UPDATE.getDetail())
                .build());
    }

}
