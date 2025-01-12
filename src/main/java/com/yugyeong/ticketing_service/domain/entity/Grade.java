package com.yugyeong.ticketing_service.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Grade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private int count; // 등급 별 총 좌석 개수

    @OneToMany(mappedBy = "grade")
    private List<Seat> seatList = new ArrayList<>();

    @Builder
    public Grade(String name, Double price, int count, List<Seat> seatList) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.seatList = seatList;
    }
}
