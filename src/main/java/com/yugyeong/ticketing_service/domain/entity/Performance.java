package com.yugyeong.ticketing_service.domain.entity;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private PerformanceStatus status;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceGrade> performanceGradeList = new ArrayList<>(); // 좌석 등급

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceSeat> performanceSeatList = new ArrayList<>(); // 좌석 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Builder
    public Performance(String name, LocalDateTime startDate, LocalDateTime endDate,
        String description, PerformanceStatus status,
        Venue venue) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = status;
        this.venue = venue;
    }

    /**
     * 공연장 업데이트 메소드
     *
     * @param name
     * @param venue
     * @param startDate
     * @param endDate
     * @param description
     */
    public void updatePerformance(String name, LocalDateTime startDate, LocalDateTime endDate,
        String description, Venue venue) {
        // 삭제 된 공연장은 수정 불가능
        if (status.equals(PerformanceStatus.DELETE)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_DELETED);
        }

        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.venue = venue;
    }

    /**
     * 공연 삭제 메소드
     */
    public void delete() {
        if (status.equals(PerformanceStatus.DELETE)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_DELETED);
        }
        this.status = PerformanceStatus.DELETE;
    }

    /**
     * 공연 취소 메소드
     */
    public void cancel() {
        if (status.equals(PerformanceStatus.DELETE)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_DELETED);
        }
        if (status.equals(PerformanceStatus.CANCEL)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_CANCELLED);
        }
        this.status = PerformanceStatus.CANCEL;
    }

    /**
     * 공연 만료 메소드
     */
    public void expire() {
        if (status.equals(PerformanceStatus.DELETE)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_DELETED);
        }
        if (status.equals(PerformanceStatus.EXPIRE)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_EXPIRED);
        }
        this.status = PerformanceStatus.EXPIRE;
    }

    public void addPerformanceGrade(PerformanceGrade performanceGrade) {
        performanceGradeList.add(performanceGrade);
        performanceGrade.changePerformance(this);
    }

    public void addPerformanceSeat(PerformanceSeat performanceSeat) {
        performanceSeatList.add(performanceSeat);
        performanceSeat.changePerformance(this);
    }
}
