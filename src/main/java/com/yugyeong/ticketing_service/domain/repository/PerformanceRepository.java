package com.yugyeong.ticketing_service.domain.repository;

import com.yugyeong.ticketing_service.domain.PerformanceStatus;
import com.yugyeong.ticketing_service.domain.entity.Performance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findByStatusNot(PerformanceStatus performanceStatus);

    Optional<Object> findByIdAndStatusNot(Long id, PerformanceStatus performanceStatus);
}