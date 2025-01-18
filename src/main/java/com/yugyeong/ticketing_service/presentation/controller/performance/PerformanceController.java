package com.yugyeong.ticketing_service.presentation.controller.performance;

import com.yugyeong.ticketing_service.application.service.performance.PerformanceService;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final PerformanceService performanceService;


    @Operation(
        summary = "공연 목록 조회",
        description = "모든 공연 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
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
        summary = "공연 정보 조회",
        description = "공연 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
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
        summary = "공연 생성",
        description = "공영을 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 생성 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> createPerformance(
        @RequestBody @Valid
        PerformanceCreateRequestDto performanceCreateRequestDto) {
        performanceService.createPerformance(performanceCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_CREATE.getTitle())
                .status(SuccessCode.PERFORMANCE_CREATE.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_CREATE.getDetail())
                .build());
    }

    @Operation(
        summary = "공연 수정",
        description = "공연을 수정합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 수정 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updatePerformance(@PathVariable("id") Long id,
        @RequestBody @Valid PerformanceUpdateRequestDto performanceUpdateRequestDto) {
        performanceService.updatePerformance(id, performanceUpdateRequestDto);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_UPDATE.getTitle())
                .status(SuccessCode.PERFORMANCE_UPDATE.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_UPDATE.getDetail())
                .build());
    }

    @Operation(
        summary = "공연 삭제",
        description = "공연을 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 삭제 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deletePerformance(@PathVariable("id") Long id) {
        performanceService.deletePerformance(id);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_DELETE.getTitle())
                .status(SuccessCode.PERFORMANCE_DELETE.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_DELETE.getDetail())
                .build());
    }

    @Operation(
        summary = "공연 취소",
        description = "공연을 취소합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 취소 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> cancelPerformance(@PathVariable("id") Long id) {
        performanceService.cancelPerformance(id);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_CANCEL.getTitle())
                .status(SuccessCode.PERFORMANCE_CANCEL.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_CANCEL.getDetail())
                .build());
    }

    @Operation(
        summary = "공연 만료",
        description = "공연을 만료시킵니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 만료 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PatchMapping("/{id}/expire")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> expirePerformance(@PathVariable("id") Long id) {
        performanceService.expirePerformance(id);

        return ResponseEntity.ok()
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_EXPIRE.getTitle())
                .status(SuccessCode.PERFORMANCE_EXPIRE.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_EXPIRE.getDetail())
                .build());
    }
}
