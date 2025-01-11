package com.yugyeong.ticketing_service.presentation.controller.performance;

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
import com.yugyeong.ticketing_service.application.service.performance.PerformanceService;
import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Seat;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.SeatCreateRequestDto;
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

    @Test
    void 공연_목록_조회_성공() throws Exception {
        // Given
        List<PerformanceResponseDto> mockPerformances = List.of(
            PerformanceResponseDto.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .dateTime(LocalDateTime.now())
                .description("Description 1")
                .price(1000.0)
                .status(PerformanceStatus.ACTIVE).build(),

            PerformanceResponseDto.builder()
                .name("Performance 2")
                .venue("Venue 2")
                .dateTime(LocalDateTime.now())
                .description("Description 2")
                .price(1000.0)
                .status(PerformanceStatus.DELETE).build()
        );

        when(performanceService.getAllPerformances()).thenReturn(mockPerformances);

        // When & Then
        mockMvc.perform(get("/performance/performances"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.performances").isArray())
            .andExpect(jsonPath("$.data.performances[0].name").value("Performance 1"));

    }

    @Test
    void 공연_조회_성공() throws Exception {
        // Given
        PerformanceResponseDto mockPerformances = PerformanceResponseDto.builder()
            .name("Performance 1")
            .venue("Venue 1")
            .dateTime(LocalDateTime.now())
            .description("Description 1")
            .price(1000.0)
            .status(PerformanceStatus.ACTIVE).build();

        when(performanceService.getPerformance(1L)).thenReturn(mockPerformances);

        // When & Then
        mockMvc.perform(get("/performance/" + "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.performance.name").value("Performance 1"))
            .andExpect(jsonPath("$.data.performance.venue").value("Venue 1"))
            .andExpect(jsonPath("$.data.performance.description").value("Description 1"))
            .andExpect(jsonPath("$.data.performance.price").value(1000.0))
            .andExpect(jsonPath("$.data.performance.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 공연_등록_성공() throws Exception {
        // given
        SeatCreateRequestDto seat1 = new SeatCreateRequestDto("S", 1000.0, 50);
        SeatCreateRequestDto seat2 = new SeatCreateRequestDto("A", 500.0, 100);

        SeatCreateRequestDto seatCreateRequestDto1 = SeatCreateRequestDto.builder()
            .grade("S")
            .price(10000.0)
            .count(50)
            .build();
        SeatCreateRequestDto seatCreateRequestDto2 = SeatCreateRequestDto.builder()
            .grade("A")
            .price(9000.0)
            .count(100)
            .build();

        PerformanceCreateRequestDto performanceCreateRequestDto = PerformanceCreateRequestDto.builder()
            .name("Performance 1")
            .venue("Venue 1")
            .dateTime(LocalDateTime.now())
            .description("A wonderful performance")
            .seatList(List.of(seatCreateRequestDto1, seatCreateRequestDto2))
            .build();

        doNothing().when(performanceService)
            .createPerformance(any(PerformanceCreateRequestDto.class));

        // when & then
        mockMvc.perform(post("/performance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(performanceCreateRequestDto))
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
        Seat seat1 = new Seat("S", 1000.0, 50);
        Seat seat2 = new Seat("A", 500.0, 100);

        PerformanceUpdateRequestDto updateRequestDto = PerformanceUpdateRequestDto.builder()
            .name("Performance 2")
            .venue("Venue 2")
            .dateTime(LocalDateTime.now())
            .description("A wonderful performance")
            .price(2000.0)
            .seatList(List.of(seat1, seat2))
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