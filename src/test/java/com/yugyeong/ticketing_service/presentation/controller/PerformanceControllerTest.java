package com.yugyeong.ticketing_service.presentation.controller;

import static com.yugyeong.ticketing_service.testutil.TestConstants.PERFORMANCE_DESCRIPTION;
import static com.yugyeong.ticketing_service.testutil.TestConstants.PERFORMANCE_GRADE_NAME;
import static com.yugyeong.ticketing_service.testutil.TestConstants.PERFORMANCE_GRADE_PRICE;
import static com.yugyeong.ticketing_service.testutil.TestConstants.PERFORMANCE_GRADE_TOTAL_SEATS;
import static com.yugyeong.ticketing_service.testutil.TestConstants.PERFORMANCE_NAME;
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
import com.yugyeong.ticketing_service.application.service.PerformanceService;
import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Venue;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceGradeCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceGradeUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.response.success.SuccessCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PerformanceController.class)
@WithMockUser
class PerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PerformanceService performanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 공연_목록_조회_성공() throws Exception {
        // Given
        Venue venue = Venue.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        List<PerformanceResponseDto> performances = List.of(
            PerformanceResponseDto.builder()
                .name(PERFORMANCE_NAME.get(0))
                .venue(venue)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description(PERFORMANCE_DESCRIPTION.get(0))
                .status(PerformanceStatus.ACTIVE)
                .build(),

            PerformanceResponseDto.builder()
                .name(PERFORMANCE_NAME.get(1))
                .venue(venue)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description(PERFORMANCE_DESCRIPTION.get(1))
                .status(PerformanceStatus.DELETE)
                .build()
        );

        when(performanceService.getAllPerformances()).thenReturn(performances);

        // When & Then
        mockMvc.perform(get("/performance/performances"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.performances[0].name").value(PERFORMANCE_NAME.get(0)))
            .andExpect(jsonPath("$.data.performances[0].venue.name").value(VENUE_NAME))
            .andExpect(
                jsonPath("$.data.performances[0].venue.description").value(VENUE_DESCRIPTION))
            .andExpect(jsonPath("$.data.performances[0].venue.totalSeats").value(VENUE_TOTAL_SEATS))
            .andExpect(
                jsonPath("$.data.performances[0].description").value(
                    PERFORMANCE_DESCRIPTION.get(0)))
            .andExpect(
                jsonPath("$.data.performances[0].status").value(
                    PerformanceStatus.ACTIVE.toString()));

    }

    @Test
    void 공연_조회_성공() throws Exception {
        // Given
        Venue venue = Venue.builder()
            .name(VENUE_NAME)
            .description(VENUE_DESCRIPTION)
            .totalSeats(VENUE_TOTAL_SEATS)
            .build();

        PerformanceResponseDto performance =
            PerformanceResponseDto.builder()
                .name(PERFORMANCE_NAME.get(0))
                .venue(venue)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description(PERFORMANCE_DESCRIPTION.get(0))
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceService.getPerformance(1L)).thenReturn(performance);

        // When & Then
        mockMvc.perform(get("/performance/" + "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.performance.name").value(PERFORMANCE_NAME.get(0)))
            .andExpect(jsonPath("$.data.performance.venue.name").value(VENUE_NAME))
            .andExpect(jsonPath("$.data.performance.venue.description").value(VENUE_DESCRIPTION))
            .andExpect(jsonPath("$.data.performance.venue.totalSeats").value(VENUE_TOTAL_SEATS))
            .andExpect(
                jsonPath("$.data.performance.description").value(PERFORMANCE_DESCRIPTION.get(0)))
            .andExpect(
                jsonPath("$.data.performance.status").value(PerformanceStatus.ACTIVE.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연_등록_성공() throws Exception {
        // given
        PerformanceGradeCreateRequestDto performanceGradeCreateRequestDto1 = PerformanceGradeCreateRequestDto.builder()
            .name(PERFORMANCE_GRADE_NAME.get(0))
            .price(PERFORMANCE_GRADE_PRICE.get(0))
            .totalSeats(PERFORMANCE_GRADE_TOTAL_SEATS.get(0))
            .build();
        PerformanceGradeCreateRequestDto performanceGradeCreateRequestDto2 = PerformanceGradeCreateRequestDto.builder()
            .name(PERFORMANCE_GRADE_NAME.get(1))
            .price(PERFORMANCE_GRADE_PRICE.get(1))
            .totalSeats(PERFORMANCE_GRADE_TOTAL_SEATS.get(1))
            .build();

        PerformanceCreateRequestDto performanceCreateRequestDto = PerformanceCreateRequestDto.builder()
            .name(PERFORMANCE_NAME.get(0))
            .venueId(1L)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .description(PERFORMANCE_DESCRIPTION.get(0))
            .performanceGradeList(
                List.of(performanceGradeCreateRequestDto1, performanceGradeCreateRequestDto2))
            .build();

        doNothing().when(performanceService)
            .createPerformance(any(PerformanceCreateRequestDto.class));

        // when & then
        mockMvc.perform(post("/performance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(performanceCreateRequestDto))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(
                jsonPath("$.status").value(SuccessCode.PERFORMANCE_CREATE.getStatus().value()))
            .andExpect(jsonPath("$.title").value(SuccessCode.PERFORMANCE_CREATE.getTitle()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.PERFORMANCE_CREATE.getDetail()));

        verify(performanceService, times(1)).createPerformance(
            any(PerformanceCreateRequestDto.class));

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연_수정_성공() throws Exception {
        // given
        PerformanceGradeUpdateRequestDto dto1 = PerformanceGradeUpdateRequestDto.builder()
            .name(PERFORMANCE_GRADE_NAME.get(0))
            .price(PERFORMANCE_GRADE_PRICE.get(0))
            .totalSeats(PERFORMANCE_GRADE_TOTAL_SEATS.get(0))
            .build();

        PerformanceGradeUpdateRequestDto dto2 = PerformanceGradeUpdateRequestDto.builder()
            .name(PERFORMANCE_GRADE_NAME.get(1))
            .price(PERFORMANCE_GRADE_PRICE.get(1))
            .totalSeats(PERFORMANCE_GRADE_TOTAL_SEATS.get(1))
            .build();

        PerformanceUpdateRequestDto updateRequestDto = PerformanceUpdateRequestDto.builder()
            .name(PERFORMANCE_NAME.get(0))
            .venueId(1L)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .description(PERFORMANCE_DESCRIPTION.get(0))
            .performanceGradeList(
                List.of(dto1, dto2))
            .build();

        doNothing().when(performanceService).updatePerformance(1L, updateRequestDto);

        // when & then
        mockMvc.perform(patch("/performance/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateRequestDto))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.status").value(SuccessCode.PERFORMANCE_UPDATE.getStatus().value()))
            .andExpect(jsonPath("$.title").value(SuccessCode.PERFORMANCE_UPDATE.getTitle()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.PERFORMANCE_UPDATE.getDetail()));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연_삭제_성공() throws Exception {
        //given
        doNothing().when(performanceService).deletePerformance(1L);

        //when & then
        mockMvc.perform(delete("/performance/1").
                with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(SuccessCode.PERFORMANCE_DELETE.getTitle()))
            .andExpect(
                jsonPath("$.status").value(SuccessCode.PERFORMANCE_DELETE.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.PERFORMANCE_DELETE.getDetail()));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연_취소_성공() throws Exception {
        //given
        doNothing().when(performanceService).cancelPerformance(1L);

        //when & then
        mockMvc.perform(patch("/performance/1/cancel").
                with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(SuccessCode.PERFORMANCE_CANCEL.getTitle()))
            .andExpect(
                jsonPath("$.status").value(SuccessCode.PERFORMANCE_CANCEL.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.PERFORMANCE_CANCEL.getDetail()));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연_만료_성공() throws Exception {
        //given
        doNothing().when(performanceService).expirePerformance(1L);

        //when & then
        mockMvc.perform(patch("/performance/1/expire")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(SuccessCode.PERFORMANCE_EXPIRE.getTitle()))
            .andExpect(
                jsonPath("$.status").value(SuccessCode.PERFORMANCE_EXPIRE.getStatus().value()))
            .andExpect(jsonPath("$.detail").value(SuccessCode.PERFORMANCE_EXPIRE.getDetail()));
    }

}