package com.yugyeong.ticketing_service.domain.repository;

import com.yugyeong.ticketing_service.domain.entity.PerformanceSeat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long> {

    Optional<PerformanceSeat> findByIdAndFalse(Long performanceSeatId);

}
