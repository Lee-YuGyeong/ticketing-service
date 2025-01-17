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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number; // 좌석 번호

    private Boolean isReserved = false; // 예약 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private PerformanceGrade performanceGrade; // 좌석 등급

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_base_id")
    private SeatBase seatBase; // 좌석 등급

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Reservation reservation;

    @Builder
    public PerformanceSeat(int number, Boolean isReserved, PerformanceGrade performanceGrade,
        SeatBase seatBase, Reservation reservation) {
        this.number = number;
        this.isReserved = isReserved;
        this.performanceGrade = performanceGrade;
        this.seatBase = seatBase;
        this.reservation = reservation;
    }
}
