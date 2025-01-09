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
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String grade;

    private Double price;

    private int count;

    @OneToMany(mappedBy = "seat")
    private List<Ticket> tickets = new ArrayList<>();

    @Builder
    public Seat(String grade, Double price, int count) {
        this.grade = grade;
        this.price = price;
        this.count = count;
    }
}
