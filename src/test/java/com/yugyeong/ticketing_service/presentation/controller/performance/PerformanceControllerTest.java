package com.yugyeong.ticketing_service.presentation.controller.performance;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yugyeong.ticketing_service.application.service.performance.PerformanceService;
import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
    void createPerformance() {
    }

    @Test
    void updatePerformance() {
    }

    @Test
    void deletePerformance() {
    }

    @Test
    void cancelPerformance() {
    }

    @Test
    void expirePerformance() {
    }
}