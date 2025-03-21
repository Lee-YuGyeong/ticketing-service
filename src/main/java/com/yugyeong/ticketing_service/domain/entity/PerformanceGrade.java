package com.yugyeong.ticketing_service.domain.entity;

import com.yugyeong.ticketing_service.presentation.exception.CustomException;
import com.yugyeong.ticketing_service.presentation.response.error.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceGrade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private int totalSeats; // 등급 별 총 좌석 개수

    private int remainSeats; // 등급 별 남은 좌석 개수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToMany(mappedBy = "performanceGrade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceSeat> performanceSeatList = new ArrayList<>();

    @Builder
    public PerformanceGrade(String name, Double price, int totalSeats, Performance performance,
        List<PerformanceSeat> performanceSeatList) {
        this.name = name;
        this.price = price;
        this.totalSeats = totalSeats;
        this.performance = performance;
        this.remainSeats = totalSeats;
    }

    public void changePerformance(Performance performance) {
        this.performance = performance;
    }

    public void addPerformanceSeat(PerformanceSeat performanceSeat) {
        performanceSeatList.add(performanceSeat);
        performanceSeat.changePerformanceGrade(this);
    }

    public void reservePerformance() {
        if (remainSeats < 1) {
            throw new CustomException(ErrorCode.PERFORMANCE_SEAT_FULL);
        }

        remainSeats -= 1;
    }

    public void cancelPerformance() {
        if (remainSeats <= 0) {
            throw new CustomException(ErrorCode.RESERVATION_CANCEL);
        }
        remainSeats--; // 예약 좌석 개수 감소
    }
}
