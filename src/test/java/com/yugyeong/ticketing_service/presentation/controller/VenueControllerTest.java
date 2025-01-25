package com.yugyeong.ticketing_service.presentation.controller;

import static com.yugyeong.ticketing_service.testutil.TestConstants.VENUE_DESCRIPTION;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VENUE_NAME;
import static com.yugyeong.ticketing_service.testutil.TestConstants.VENUE_TOTAL_SEATS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugyeong.ticketing_service.application.service.VenueService;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.venue.VenueUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VenueController.class)
@WithMockUser
class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VenueService venueService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 공연장_목록_조회_성공() throws Exception {
        // given
        List<VenueResponseDto> venues = List.of(
            VenueResponseDto.builder()
                .name(VENUE_NAME)
                .description(VENUE_DESCRIPTION)
                .totalSeats(VENUE_TOTAL_SEATS)
                .build()
        );

        when(venueService.getAllVenues()).thenReturn(venues);

        // When & Then
        mockMvc.perform(get("/venue/venues"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.venues[0].name").value(VENUE_NAME))
            .andExpect(jsonPath("$.data.venues[0].description").value(VENUE_DESCRIPTION))
            .andExpect(jsonPath("$.data.venues[0].totalSeats").value(VENUE_TOTAL_SEATS))
            .andExpect(jsonPath("$.title").value(SuccessCode.VENUE_FOUND.getTitle()))
            .andExpect(jsonPath("$.status").value(SuccessCode.VENUE_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.VENUE_FOUND.getDetail()));
    }

    @Test
    void 공연장_조회_성공() throws Exception {
        // given
        VenueResponseDto venue =
            VenueResponseDto.builder()
                .name(VENUE_NAME)
                .description(VENUE_DESCRIPTION)
                .totalSeats(VENUE_TOTAL_SEATS)
                .build();

        when(venueService.getVenue(1L)).thenReturn(venue);

        // When & Then
        mockMvc.perform(get("/venue/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.venue.name").value(VENUE_NAME))
            .andExpect(jsonPath("$.data.venue.description").value(VENUE_DESCRIPTION))
            .andExpect(jsonPath("$.data.venue.totalSeats").value(VENUE_TOTAL_SEATS))
            .andExpect(jsonPath("$.title").value(SuccessCode.VENUE_FOUND.getTitle()))
            .andExpect(jsonPath("$.status").value(SuccessCode.VENUE_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.VENUE_FOUND.getDetail()));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연장_생성_성공() throws Exception {
        // given
        VenueCreateRequestDto venueCreateRequestDto = VenueCreateRequestDto.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        doNothing().when(venueService)
            .createVenue(any(VenueCreateRequestDto.class));

        // when & then
        mockMvc.perform(post("/venue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venueCreateRequestDto))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(
                jsonPath("$.status").value(SuccessCode.VENUE_CREATE.getStatus().value()))
            .andExpect(jsonPath("$.title").value(SuccessCode.VENUE_CREATE.getTitle()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.VENUE_CREATE.getDetail()));

        verify(venueService, times(1)).createVenue(
            any(VenueCreateRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연장_수정_성공() throws Exception {
        // given
        VenueUpdateRequestDto venueUpdateRequestDto = VenueUpdateRequestDto.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        doNothing().when(venueService).updateVenue(1L, venueUpdateRequestDto);

        // when & then
        mockMvc.perform(patch("/venue/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(venueUpdateRequestDto))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.status").value(SuccessCode.VENUE_UPDATE.getStatus().value()))
            .andExpect(jsonPath("$.title").value(SuccessCode.VENUE_UPDATE.getTitle()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.VENUE_UPDATE.getDetail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연장_삭제_성공() throws Exception {
        //given
        doNothing().when(venueService).deleteVenue(1L);

        //when & then
        mockMvc.perform(delete("/venue/1").
                with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(SuccessCode.VENUE_DELETE.getTitle()))
            .andExpect(
                jsonPath("$.status").value(SuccessCode.VENUE_DELETE.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.VENUE_DELETE.getDetail()));
    }
}