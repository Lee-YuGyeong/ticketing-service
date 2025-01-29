package com.yugyeong.ticketing_service.presentation.dto.reservation;

import com.yugyeong.ticketing_service.domain.entity.Performance;
import com.yugyeong.ticketing_service.domain.entity.PerformanceSeat;
import com.yugyeong.ticketing_service.domain.entity.ReservationStatus;
import com.yugyeong.ticketing_service.domain.entity.User;
import lombok.Builder;

@Builder
public class ReservationResponseDto {

    private Double price;

    private ReservationStatus reservationStatus;

    private Performance performance;

    private PerformanceSeat performanceSeat;

    private User user;
}
