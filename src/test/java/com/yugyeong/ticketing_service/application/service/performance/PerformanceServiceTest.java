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
            new Performance("Performance 1", "Venue 1", LocalDateTime.now(), "Description 1",
                1000.0, PerformanceStatus.ACTIVE),
            new Performance("Performance 2", "Venue 2", LocalDateTime.now().plusDays(1),
                "Description 2", 1500.0, PerformanceStatus.DELETE)
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
            new Performance("Performance 1", "Venue 1", LocalDateTime.now(), "Description 1",
                1000.0, PerformanceStatus.ACTIVE)
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

        Performance mockPerformances = new Performance("Performance 1", "Venue 1",
            LocalDateTime.now(), "Description 1", 1000.0, PerformanceStatus.DELETE);
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

        Performance mockPerformances = new Performance("Performance 1", "Venue 1",
            LocalDateTime.now(), "Description 1", 1000.0, PerformanceStatus.ACTIVE);
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
        PerformanceCreateRequestDto performanceCreateRequestDto = new PerformanceCreateRequestDto(
            "Performance 1", "Venue 1",
            LocalDateTime.now(), "Description 1", 1000.0);

        //when
        performanceService.createPerformance(performanceCreateRequestDto);

        //then
        verify(performanceRepository, times(1))
            .save(argThat(performance -> {
                assertAll("Performance",
                    () -> assertEquals("Performance 1", performance.getName()),
                    () -> assertEquals("Venue 1", performance.getVenue()),
                    () -> assertEquals("Description 1", performance.getDescription()),
                    () -> assertEquals(1000.0, performance.getPrice())
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

        Performance performance = new Performance("Performance 1", "Venue 1", LocalDateTime.now(),
            "Description 1", 1000.0, PerformanceStatus.ACTIVE);
        when(performanceRepository.findById(1L)).thenReturn(Optional.of(performance));

        PerformanceUpdateRequestDto performanceUpdateRequestDto = new PerformanceUpdateRequestDto(
            newName, newVenue, LocalDateTime.now(), newDescription, newPrice);

        //when
        performanceService.updatePerformance(1L, performanceUpdateRequestDto);

        //then
        assertThat(performance.getName()).isEqualTo(newName);
        assertThat(performance.getVenue()).isEqualTo(newVenue);
        assertThat(performance.getDescription()).isEqualTo(newDescription);
        assertThat(performance.getPrice()).isEqualTo(newPrice);
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

        PerformanceUpdateRequestDto performanceUpdateRequestDto = new PerformanceUpdateRequestDto(
            newName, newVenue, LocalDateTime.now(), newDescription, newPrice);

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
        Performance performance = new Performance("Performance 1", "Venue 1", LocalDateTime.now(),
            "Description 1", 1000.0, PerformanceStatus.ACTIVE);
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
        Performance performance = new Performance("Performance 1", "Venue 1", LocalDateTime.now(),
            "Description 1", 1000.0, PerformanceStatus.ACTIVE);
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
        Performance performance = new Performance("Performance 1", "Venue 1", LocalDateTime.now(),
            "Description 1", 1000.0, PerformanceStatus.ACTIVE);
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
        Performance performance = new Performance("Performance 1", "Venue 1", LocalDateTime.now(),
            "Description 1", 1000.0, PerformanceStatus.ACTIVE);
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
        Performance performance = new Performance("Performance 1", "Venue 1", LocalDateTime.now(),
            "Description 1", 1000.0, PerformanceStatus.ACTIVE);
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
        Performance performance = new Performance("Performance 1", "Venue 1", LocalDateTime.now(),
            "Description 1", 1000.0, PerformanceStatus.ACTIVE);
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