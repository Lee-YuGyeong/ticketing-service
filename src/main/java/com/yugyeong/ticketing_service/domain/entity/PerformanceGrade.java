package com.yugyeong.ticketing_service.domain.entity;

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

    private int total_seats; // 등급 별 총 좌석 개수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToMany(mappedBy = "grade")
    private List<PerformanceSeat> performanceSeatList = new ArrayList<>();

    @Builder
    public PerformanceGrade(String name, Double price, int total_seats, Performance performance,
        List<PerformanceSeat> performanceSeatList) {
        this.name = name;
        this.price = price;
        this.total_seats = total_seats;
        this.performance = performance;
        this.performanceSeatList = performanceSeatList;
    }

    public void changePerformance(Performance performance) {
        this.performance = performance;
    }
}
