package com.yugyeong.ticketing_service.presentation.dto.reservation;

import com.yugyeong.ticketing_service.domain.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReservationResponseDto {

    private Double price;
    private ReservationStatus reservationStatus;
    private Long performanceId;
    private Long seatNumberId;
    private String userEmail;
}
