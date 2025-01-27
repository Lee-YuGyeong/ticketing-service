package com.yugyeong.ticketing_service.application.service;

import com.yugyeong.ticketing_service.domain.repository.PerformanceSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SeatService {

    private PerformanceSeatRepository performanceSeatRepository;

    @Transactional(readOnly = true)
    public void getSeat() {

    }

}
