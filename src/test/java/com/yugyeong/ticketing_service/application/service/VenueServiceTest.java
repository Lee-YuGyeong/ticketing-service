package com.yugyeong.ticketing_service.application.service;

import static com.yugyeong.ticketing_service.testutil.TestConstants.VENUE_DESCRIPTION;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VENUE_NAME;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VENUE_TOTAL_SEATS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yugyeong.ticketing_service.domain.Role;
import com.yugyeong.ticketing_service.domain.entity.Venue;
import com.yugyeong.ticketing_service.domain.repository.VenueRepository;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Test
    void 공연장_목록_조회_관리자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.ADMIN));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        List<Venue> mockVenues = List.of(
            Venue.builder()
                .name(VENUE_NAME)
                .description(VENUE_DESCRIPTION)
                .totalSeats(VENUE_TOTAL_SEATS)
                .build()
        );

        when(venueRepository.findAll()).thenReturn(mockVenues);

        // when
        List<VenueResponseDto> venues = venueService.getAllVenues();

        // then
        assertThat(venues).hasSize(1);
        assertThat(venues).extracting("name")
            .containsExactly(VENUE_NAME);
    }

    @Test
    void 공연장_목록_조회_일반_사용자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.USER));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        List<Venue> mockVenues = List.of(
            Venue.builder()
                .name(VENUE_NAME)
                .description(VENUE_DESCRIPTION)
                .totalSeats(VENUE_TOTAL_SEATS)
                .build()
        );

        when(venueRepository.findByStatus(true)).thenReturn(mockVenues);

        // when
        List<VenueResponseDto> venues = venueService.getAllVenues();

        // then
        assertThat(venues).hasSize(1);
        assertThat(venues).extracting("name")
            .containsExactly(VENUE_NAME);
        assertThat(venues).extracting("status")
            .containsExactly(true);
    }

    @Test
    void 공연장_조회_관리자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.ADMIN));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        Venue mockVenue =
            Venue.builder()
                .name(VENUE_NAME)
                .description(VENUE_DESCRIPTION)
                .totalSeats(VENUE_TOTAL_SEATS)
                .build();

        when(venueRepository.findById(1L)).thenReturn(Optional.of(mockVenue));

        // when
        venueService.getVenue(1L);

        // then
        assertThat(mockVenue.getName()).isEqualTo(VENUE_NAME);
        assertThat(mockVenue.getDescription()).isEqualTo(VENUE_DESCRIPTION);
        assertThat(mockVenue.getTotalSeats()).isEqualTo(VENUE_TOTAL_SEATS);
    }

    @Test
    void 공연장_조회_일반_사용자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.USER));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        Venue mockVenue =
            Venue.builder()
                .name(VENUE_NAME)
                .description(VENUE_DESCRIPTION)
                .totalSeats(VENUE_TOTAL_SEATS)
                .build();

        when(venueRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(mockVenue));

        // when
        venueService.getVenue(1L);

        // then
        assertThat(mockVenue.getName()).isEqualTo(VENUE_NAME);
        assertThat(mockVenue.getDescription()).isEqualTo(VENUE_DESCRIPTION);
        assertThat(mockVenue.getTotalSeats()).isEqualTo(VENUE_TOTAL_SEATS);
    }

    @Test
    void 공연장_생성() {
        // given
        VenueCreateRequestDto venueCreateRequestDto = VenueCreateRequestDto.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        // when
        venueService.createVenue(venueCreateRequestDto);

        //then
        verify(venueRepository, times(1))
            .save(argThat(venue -> {
                assertAll("venue",
                    () -> assertEquals(VENUE_NAME, venue.getName()),
                    () -> assertEquals(VENUE_DESCRIPTION, venue.getDescription()),
                    () -> assertEquals(VENUE_TOTAL_SEATS, venue.getTotalSeats())
                );
                return true;
            }));
    }

    @Test
    void 공연장_수정() {
        // given
        String newName = "newName";
        String newDescription = "newDescription";
        int newTotalSeats = 30;

        VenueUpdateRequestDto venueUpdateRequestDto = VenueUpdateRequestDto.builder()
            .name(newName)
            .description(newDescription)
            .totalSeats(newTotalSeats)
            .build();

        Venue mockVenue = Venue.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        when(venueRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(mockVenue));

        // when
        venueService.updateVenue(1L, venueUpdateRequestDto);

        //then
        assertThat(mockVenue.getName()).isEqualTo(newName);
        assertThat(mockVenue.getDescription()).isEqualTo(newDescription);
        assertThat(mockVenue.getTotalSeats()).isEqualTo(newTotalSeats);
    }

    @Test
    void 공연장_수정_실패() {
        // given
        VenueUpdateRequestDto venueUpdateRequestDto = VenueUpdateRequestDto.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> venueService.updateVenue(1L, venueUpdateRequestDto));

        // then
        assertEquals(ErrorCode.VENUE_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.VENUE_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void 공연장_삭제() {
        // given
        Venue mockVenue = Venue.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        when(venueRepository.findById(1L)).thenReturn(Optional.of(mockVenue));

        // when
        venueService.deleteVenue(1L);

        // then
        assertFalse(mockVenue.isStatus());
    }

    @Test
    void 공연장_삭제_실패() {
        //given
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> venueService.deleteVenue(1L));

        //then
        assertEquals(ErrorCode.VENUE_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.VENUE_NOT_FOUND, exception.getErrorCode());

    }
}