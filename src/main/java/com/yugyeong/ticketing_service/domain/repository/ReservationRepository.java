package com.yugyeong.ticketing_service.domain.repository;

import com.yugyeong.ticketing_service.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
