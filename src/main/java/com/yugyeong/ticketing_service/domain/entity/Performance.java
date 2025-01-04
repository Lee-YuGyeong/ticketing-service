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
public class Performance extends BaseEntity {

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
        this.name = name;
        this.venue = venue;
        this.dateTime = dateTime;
        this.description = description;
        this.price = price;
    }

}
