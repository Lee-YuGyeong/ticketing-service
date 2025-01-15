package com.yugyeong.ticketing_service.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number; // 좌석 번호

    private Boolean isReserved = false; // 예약 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade; // 좌석 등급

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToOne(mappedBy = "seat", fetch = FetchType.LAZY)
    private Reservation reservation;

    @Builder
    public Seat(int number, Boolean isReserved, Grade grade, Performance performance,
        Reservation reservation) {
        this.number = number;
        this.isReserved = isReserved;
        this.grade = grade;
        this.performance = performance;
        this.reservation = reservation;
    }
}
