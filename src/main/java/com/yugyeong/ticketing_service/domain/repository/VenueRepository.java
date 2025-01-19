package com.yugyeong.ticketing_service.domain.repository;

import com.yugyeong.ticketing_service.domain.entity.Venue;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    Optional<Venue> findByIdAndStatus(Long id, boolean status);

    List<Venue> findByStatus(boolean status);
}
