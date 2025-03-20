package com.yugyeong.ticketing_service.domain.repository;

import com.yugyeong.ticketing_service.domain.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByPerformanceId(Long performanceId);

    List<Reservation> findByUserId(Long id);

    List<Reservation> findByUserIdAndPerformanceId(Long id, Long performanceId);
}
