package com.yugyeong.ticketing_service.application.service.performance;

import com.yugyeong.ticketing_service.domain.entity.Venue;
import com.yugyeong.ticketing_service.domain.repository.VenueRepository;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VenueService {

    private final VenueRepository venueRepository;

    public void createVenue(VenueCreateRequestDto venueCreateRequestDto) {
        Venue venue = Venue.builder()
            .name(venueCreateRequestDto.getName())
            .description(venueCreateRequestDto.getDescription())
            .totalSeats(venueCreateRequestDto.getTotalSeats())
            .build();

        venueRepository.save(venue);
    }

    public void updateVenue(Long id, VenueUpdateRequestDto venueUpdateRequestDto) {
        //공연장 유효성 확인
        Venue venue = venueRepository.findByIdAndStatus(id, true)
            .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        venue.changeVenue(
            venueUpdateRequestDto.getName(),
            venueUpdateRequestDto.getDescription(),
            venueUpdateRequestDto.getTotalSeats()
        );
        
    }
}
