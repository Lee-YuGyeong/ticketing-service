package com.yugyeong.ticketing_service.domain.entity;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String venue;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    private int remainCount; // 남은 좌석 개수

    @Enumerated(EnumType.STRING)
    private PerformanceStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> gradeList = new ArrayList<>(); // 좌석 등급


    @Builder
    public Performance(String name, String venue, LocalDateTime startDate, LocalDateTime endDate,
        String description, PerformanceStatus status, List<Grade> gradeList, int remainCount) {
        this.name = name;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = status;
        this.gradeList = gradeList;
        this.remainCount = remainCount;
    }


    /**
     * 공연장 업데이트 메소드
     *
     * @param name
     * @param venue
     * @param startDate
     * @param endDate
     * @param description
     * @param gradeList
     */
    public void updatePerformance(String name, String venue, LocalDateTime startDate,
        LocalDateTime endDate,
        String description, List<Grade> gradeList) {
        if (status.equals(PerformanceStatus.DELETE)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_DELETED);
        }

        this.name = name;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.gradeList = gradeList;
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
}
