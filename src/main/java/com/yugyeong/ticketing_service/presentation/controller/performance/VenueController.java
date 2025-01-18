package com.yugyeong.ticketing_service.presentation.controller.performance;

import com.yugyeong.ticketing_service.application.service.performance.VenueService;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/venue")
public class VenueController {

    private final VenueService venueService;

    public RequestEntity<SuccessResponse> createVenue() {

    }

}
