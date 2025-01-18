package com.yugyeong.ticketing_service.presentation.controller.performance;

import com.yugyeong.ticketing_service.application.service.performance.VenueService;
import com.yugyeong.ticketing_service.presentation.dto.performance.VenueCreateRequestDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/venue")
public class VenueController {

    private final VenueService venueService;

    @Operation(
        summary = "공연장 생성",
        description = "공연장을 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연장 생성 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> createVenue(
        @RequestBody @Valid VenueCreateRequestDto venueCreateRequestDto) {

        venueService.createVenue(venueCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.builder()
                .title(SuccessCode.VENUE_CREATE.getTitle())
                .status(SuccessCode.VENUE_CREATE.getStatus().value())
                .detail(SuccessCode.VENUE_CREATE.getDetail())
                .build());
    }

}
