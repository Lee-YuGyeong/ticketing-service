package com.yugyeong.ticketing_service.presentation.controller;

import com.yugyeong.ticketing_service.application.service.ReservationService;
import com.yugyeong.ticketing_service.presentation.dto.reservation.ReservationCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.reservation.ReservationResponseDto;
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
import org.springframework.web.bind.annotation.PathVariable;
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
                .title(SuccessCode.RESERVATION_CREATE.getTitle())
                .status(SuccessCode.RESERVATION_CREATE.getStatus().value())
                .detail(SuccessCode.RESERVATION_CREATE.getDetail())
                .build());

    }

    @Operation(
        summary = "예약 목록 조회",
        description = "예약 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getReservations(@PathVariable("id") Long id) {
        List<ReservationResponseDto> reservations = reservationService.getReservations(id);

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.builder()
                .title(SuccessCode.RESERVATION_FOUND.getTitle())
                .status(SuccessCode.RESERVATION_FOUND.getStatus().value())
                .detail(SuccessCode.RESERVATION_FOUND.getDetail())
                .data(Map.of(
                    "reservations", reservations
                ))
                .build());

    }

    @Operation(
        summary = "예약 취소",
        description = "예약을 취소합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예약 취소 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> cancelReservation(@PathVariable("id") Long id) {
        reservationService.cancelReservation(id);

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.builder()
                .title(SuccessCode.RESERVATION_CANCEL.getTitle())
                .status(SuccessCode.RESERVATION_CANCEL.getStatus().value())
                .detail(SuccessCode.RESERVATION_CANCEL.getDetail())
                .build());

    }

}
