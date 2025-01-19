package com.yugyeong.ticketing_service.presentation.dto.venue;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VenueResponseDto {

    private String name;
    private String description;
    private int totalSeats;
    private boolean status;

}
