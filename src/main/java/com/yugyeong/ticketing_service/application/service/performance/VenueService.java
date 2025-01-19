package com.yugyeong.ticketing_service.application.service.performance;

import com.yugyeong.ticketing_service.domain.entity.Venue;
import com.yugyeong.ticketing_service.domain.repository.VenueRepository;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<VenueResponseDto> getAllVenues() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        List<Venue> venues = null;

        //관리자만 삭제된 공연 조회 가능
        if (isAdmin) {
            venues = venueRepository.findAll();
        } else {
            venues = venueRepository.findByStatus(false);
        }

        return venues.stream()
            .map(venue -> VenueResponseDto.builder()
                .name(venue.getName())
                .description(venue.getDescription())
                .totalSeats(venue.getTotalSeats())
                .status(venue.isStatus())
                .build())
            .collect(Collectors.toList());
    }

    public VenueResponseDto getVenue(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Venue venue = null;

        //관리자만 삭제된 공연 조회 가능
        if (isAdmin) {
            venue = venueRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
        } else {
            venue = venueRepository.findByIdAndStatus(id, false)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
        }

        return VenueResponseDto.builder()
            .name(venue.getName())
            .description(venue.getDescription())
            .totalSeats(venue.getTotalSeats())
            .status(venue.isStatus())
            .build();
    }
}
