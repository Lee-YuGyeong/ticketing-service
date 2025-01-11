package com.yugyeong.ticketing_service.application;

import com.yugyeong.ticketing_service.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SeatService {

    private SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public void getSeat() {

    }

}
