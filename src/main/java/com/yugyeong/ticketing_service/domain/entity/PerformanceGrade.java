package com.yugyeong.ticketing_service.domain.entity;

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

    private int remainSeats; // 남은 좌석 개수

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
        this.performanceSeatList = performanceSeatList;
        this.remainSeats = totalSeats;
    }

    public void changePerformance(Performance performance) {
        this.performance = performance;
    }

    public void changePerformanceSeat(List<PerformanceSeat> performanceSeats) {
        if (this.performanceSeatList != null) {
            this.performanceSeatList.clear();
        }

        for (PerformanceSeat performanceSeat : performanceSeats) {
            performanceSeat.changePerformanceGrade(this);
            this.performanceSeatList.add(performanceSeat);
        }
    }
}
