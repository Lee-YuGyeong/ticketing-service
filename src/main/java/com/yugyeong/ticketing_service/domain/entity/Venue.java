package com.yugyeong.ticketing_service.domain.entity;

import jakarta.persistence.CascadeType;
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
public class Venue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int totalSeats;

    private boolean status = true;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Performance> performanceList = new ArrayList<>();

    @Builder
    public Venue(String name, String description, int totalSeats,
        List<Performance> performanceList) {
        this.name = name;
        this.description = description;
        this.totalSeats = totalSeats;
        this.performanceList = performanceList;
    }

    public void changeVenue(String name, String description, int totalSeats) {
        this.name = name;
        this.description = description;
        this.totalSeats = totalSeats;
    }

    public void delete() {
        this.status = false;
    }
}
