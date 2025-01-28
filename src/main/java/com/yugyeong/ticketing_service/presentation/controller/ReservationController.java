package com.yugyeong.ticketing_service.presentation.controller;

import com.yugyeong.ticketing_service.application.service.ReservationService;
import com.yugyeong.ticketing_service.presentation.dto.reservation.ReservationCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
        summary = "공연 예약",
        description = "공연을 에약합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 예약 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse> createReservation(
        @RequestBody @Valid ReservationCreateRequestDto reservationCreateRequestDto) {
        reservationService.createReservation(reservationCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.builder()
                .title(SuccessCode.PERFORMANCE_CREATE.getTitle())
                .status(SuccessCode.PERFORMANCE_CREATE.getStatus().value())
                .detail(SuccessCode.PERFORMANCE_CREATE.getDetail())
                .build());

    }
}
