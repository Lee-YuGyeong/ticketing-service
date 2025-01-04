package com.yugyeong.ticketing_service.domain.entity;

import jakarta.persistence.Entity;
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
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String venue;

    private LocalDateTime dateTime;

    private String description;

    private Double price;

    @Builder
    public Performance(String name, String venue, LocalDateTime dateTime,
        String description, Double price) {
        this.name = name;
        this.venue = venue;
        this.dateTime = dateTime;
        this.description = description;
        this.price = price;
    }

    public void updatePerformance(String name, String venue, LocalDateTime dateTime,
        String description, Double price) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (venue != null && !venue.isEmpty()) {
            this.venue = venue;
        }
        if (dateTime != null) {
            this.dateTime = dateTime;
        }
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
        if (price != null) {
            this.price = price;
        }
    }

}
