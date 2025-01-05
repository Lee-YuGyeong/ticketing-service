package com.yugyeong.ticketing_service.domain.entity;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
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

    private LocalDateTime dateTime;

    private String description;

    private Double price;

    @Enumerated(EnumType.STRING)
    private PerformanceStatus status;

    @Builder
    public Performance(String name, String venue, LocalDateTime dateTime,
        String description, Double price, PerformanceStatus status) {
        this.name = name;
        this.venue = venue;
        this.dateTime = dateTime;
        this.description = description;
        this.price = price;
        this.status = status;
    }

    /**
     * 공연장 업데이트 메소드
     *
     * @param name
     * @param venue
     * @param dateTime
     * @param description
     * @param price
     */
    public void updatePerformance(String name, String venue, LocalDateTime dateTime,
        String description, Double price) {
        if (status.equals(PerformanceStatus.DELETE)) {
            throw new CustomException(ErrorCode.PERFORMANCE_ALREADY_DELETED);
        }
        
        this.name = name;
        this.venue = venue;
        this.dateTime = dateTime;
        this.description = description;
        this.price = price;
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
