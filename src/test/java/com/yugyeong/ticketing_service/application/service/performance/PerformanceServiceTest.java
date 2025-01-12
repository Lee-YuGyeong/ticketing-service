package com.yugyeong.ticketing_service.application.service.performance;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.Role;
import com.yugyeong.ticketing_service.domain.entity.Performance;
import com.yugyeong.ticketing_service.domain.repository.PerformanceRepository;
import com.yugyeong.ticketing_service.domain.repository.SeatRepository;
import com.yugyeong.ticketing_service.presentation.dto.performance.GradeCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.GradeUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceCreateRequestDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceResponseDto;
import com.yugyeong.ticketing_service.presentation.dto.performance.PerformanceUpdateRequestDto;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import java.time.LocalDateTime;
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
class PerformanceServiceTest {

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private PerformanceService performanceService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Test
    void 공연_전체_목록_조회_성공_관리자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.ADMIN));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        List<Performance> mockPerformances = List.of(
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build(),

            Performance.builder()
                .name("Performance 2")
                .venue("Venue 2")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 2")
                .status(PerformanceStatus.DELETE)
                .build()
        );

        when(performanceRepository.findAll()).thenReturn(mockPerformances);

        // when
        List<PerformanceResponseDto> performances = performanceService.getAllPerformances();

        // then
        assertThat(performances).hasSize(2);
        assertThat(performances).extracting("name")
            .containsExactly("Performance 1", "Performance 2");
    }

    @Test
    void 공연_전체_목록_조회_성공_관리자_외() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.USER));

        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        List<Performance> mockPerformances = List.of(
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build()
        );

        when(performanceRepository.findByStatusNot(PerformanceStatus.DELETE)).thenReturn(
            mockPerformances);

        // when
        List<PerformanceResponseDto> performances = performanceService.getAllPerformances();

        // then
        assertThat(performances).hasSize(1);
        assertThat(performances).extracting("name")
            .containsExactly("Performance 1");
    }

    @Test
    void 공연_조회_성공_관리자() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.ADMIN));
        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        Performance mockPerformances =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.DELETE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(mockPerformances));

        // when
        PerformanceResponseDto performances = performanceService.getPerformance(1L);

        // then
        assertThat(performances.getName()).isEqualTo("Performance 1");
        assertThat(performances.getVenue()).isEqualTo("Venue 1");
        assertThat(performances.getStatus()).isEqualTo(PerformanceStatus.DELETE);
    }

    @Test
    void 공연_조회_성공_관리자_외() {
        // given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Collection<SimpleGrantedAuthority> grantedAuthorities = Lists.newArrayList(
            new SimpleGrantedAuthority("ROLE_" + Role.USER));
        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        Performance mockPerformances =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findByIdAndStatusNot(1L, PerformanceStatus.DELETE)).thenReturn(
            Optional.of(mockPerformances));

        // when
        PerformanceResponseDto performances = performanceService.getPerformance(1L);

        // then
        assertThat(performances.getName()).isEqualTo("Performance 1");
        assertThat(performances.getVenue()).isEqualTo("Venue 1");
        assertThat(performances.getStatus()).isEqualTo(PerformanceStatus.ACTIVE);
    }

    @Test
    void 공연_등록_성공() {
        //given
        GradeCreateRequestDto gradeCreateRequestDto1 = GradeCreateRequestDto.builder()
            .name("S")
            .price(10000.0)
            .count(50)
            .build();
        GradeCreateRequestDto gradeCreateRequestDto2 = GradeCreateRequestDto.builder()
            .name("A")
            .price(9000.0)
            .count(100)
            .build();

        PerformanceCreateRequestDto performanceCreateRequestDto = PerformanceCreateRequestDto.builder()
            .name("Performance 1")
            .venue("Venue 1")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .description("A wonderful performance")
            .gradeList(List.of(gradeCreateRequestDto1, gradeCreateRequestDto2))
            .build();

        //when
        performanceService.createPerformance(performanceCreateRequestDto);

        //then
        verify(performanceRepository, times(1))
            .save(argThat(performance -> {
                assertAll("Performance",
                    () -> assertEquals("Performance 1", performance.getName()),
                    () -> assertEquals("Venue 1", performance.getVenue()),
                    () -> assertEquals("A wonderful performance", performance.getDescription())
                );
                return true;
            }));
    }

    @Test
    void 공연_수정_성공() {
        //given
        String newName = "New Performance";
        String newVenue = "New Venue";
        String newDescription = "New Description";
        Double newPrice = 2000.0;

        Performance performance =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));

        GradeUpdateRequestDto dto1 = GradeUpdateRequestDto.builder()
            .name("S")
            .price(10000.0)
            .count(50)
            .build();
        GradeUpdateRequestDto dto2 = GradeUpdateRequestDto.builder()
            .name("A")
            .price(9000.0)
            .count(100)
            .build();

        PerformanceUpdateRequestDto performanceUpdateRequestDto = PerformanceUpdateRequestDto.builder()
            .name(newName)
            .venue(newVenue)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .description(newDescription)
            .gradeList(List.of(dto1, dto2))
            .build();

        //when
        performanceService.updatePerformance(1L, performanceUpdateRequestDto);

        //then
        assertThat(performance.getName()).isEqualTo(newName);
        assertThat(performance.getVenue()).isEqualTo(newVenue);
        assertThat(performance.getDescription()).isEqualTo(newDescription);
        verify(performanceRepository, times(1)).findById(1L);
    }

    @Test
    void 공연_수정_실패() {
        //given
        String newName = "New Performance";
        String newVenue = "New Venue";
        String newDescription = "New Description";
        Double newPrice = 2000.0;

        when(performanceRepository.findById(1L)).thenReturn(Optional.empty());

        GradeUpdateRequestDto dto1 = GradeUpdateRequestDto.builder()
            .name("S")
            .price(10000.0)
            .count(50)
            .build();
        GradeUpdateRequestDto dto2 = GradeUpdateRequestDto.builder()
            .name("A")
            .price(9000.0)
            .count(100)
            .build();

        PerformanceUpdateRequestDto performanceUpdateRequestDto = PerformanceUpdateRequestDto.builder()
            .name(newName)
            .venue(newVenue)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .description(newDescription)
            .gradeList(List.of(dto1, dto2))
            .build();

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> performanceService.updatePerformance(1L, performanceUpdateRequestDto));

        //then
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 공연_삭제_성공() {
        //given
        Performance performance =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));

        //when
        performanceService.deletePerformance(1L);

        //then
        assertEquals(performance.getStatus(), PerformanceStatus.DELETE);
        verify(performanceRepository).save(performance);
    }

    @Test
    void 공연_삭제_실패_공연_없는_경우() {
        //given
        when(performanceRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> performanceService.deletePerformance(1L));

        //then
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 공연_삭제_실패_이미_삭제됨() {
        //given
        Performance performance =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));
        performance.delete();

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> performanceService.deletePerformance(1L));

        //then
        assertEquals(ErrorCode.PERFORMANCE_ALREADY_DELETED, exception.getErrorCode());
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void 공연_취소_성공() {
        //given
        Performance performance =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));

        //when
        performanceService.cancelPerformance(1L);

        //then
        assertEquals(performance.getStatus(), PerformanceStatus.CANCEL);
        verify(performanceRepository).save(performance);
    }

    @Test
    void 공연_취소_실패_공연_없는_경우() {
        //given
        when(performanceRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> performanceService.cancelPerformance(1L));

        //then
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 공연_취소_실패_이미_취소됨() {
        //given
        Performance performance =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));
        performance.cancel();

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> performanceService.cancelPerformance(1L));

        //then
        assertEquals(ErrorCode.PERFORMANCE_ALREADY_CANCELLED, exception.getErrorCode());
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void 공연_만료_성공() {
        //given
        Performance performance =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));

        //when
        performanceService.expirePerformance(1L);

        //then
        assertEquals(performance.getStatus(), PerformanceStatus.EXPIRE);
        verify(performanceRepository).save(performance);
    }

    @Test
    void 공연_만료_실패_공연_없는_경우() {
        //given
        when(performanceRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> performanceService.expirePerformance(1L));

        //then
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND.getDetail(), exception.getMessage());
        assertEquals(ErrorCode.PERFORMANCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 공연_만료_실패_이미_취소됨() {
        //given
        Performance performance =
            Performance.builder()
                .name("Performance 1")
                .venue("Venue 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .description("Description 1")
                .status(PerformanceStatus.ACTIVE)
                .build();

        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));
        performance.expire();

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> performanceService.expirePerformance(1L));

        //then
        assertEquals(ErrorCode.PERFORMANCE_ALREADY_EXPIRED, exception.getErrorCode());
        verify(performanceRepository, never()).save(any());
    }

}