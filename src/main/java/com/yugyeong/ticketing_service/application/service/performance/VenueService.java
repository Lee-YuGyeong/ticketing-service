package com.yugyeong.ticketing_service.application.service.performance;

import com.yugyeong.ticketing_service.domain.entity.Venue;
import com.yugyeong.ticketing_service.domain.repository.VenueRepository;
import com.yugyeong.ticketing_service.presentation.dto.performance.VenueCreateRequestDto;
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
}
