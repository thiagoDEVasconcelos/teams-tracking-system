package com.teamstracking.backend.service;

import com.teamstracking.backend.entity.LocationHistory;
import com.teamstracking.backend.repository.LocationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationHistoryRepository locationHistoryRepository;

    public List<LocationHistory> getAgentRoutByDate(Long agentId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return locationHistoryRepository
                .findByAgentIdAndRecordedAtBetweenOrderByRecordedAtAsc(agentId, start, end);
    }
}