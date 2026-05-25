package com.teamstracking.backend.service;

import com.teamstracking.backend.dto.request.CheckInRequest;
import com.teamstracking.backend.entity.Agent;
import com.teamstracking.backend.entity.CheckIn;
import com.teamstracking.backend.repository.AgentRepository;
import com.teamstracking.backend.repository.CheckInRepository;
import com.teamstracking.backend.util.HaversineUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final AgentRepository agentRepository;
    private final RealtimeEventService realtimeEventService;

    public CheckIn createManual(CheckInRequest request) {
        Agent agent = agentRepository.findById(request.getAgentId())
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado: " + request.getAgentId()));

        CheckIn lastCheckIn = checkInRepository
                .findTopByAgentIdOrderByOccurredAtDesc(agent.getId())
                .orElse(null);

        Double distance = null;
        if (lastCheckIn != null
                && lastCheckIn.getLatitude() != null
                && lastCheckIn.getLongitude() != null
                && request.getLatitude() != null
                && request.getLongitude() != null) {
            distance = HaversineUtil.calculate(
                    lastCheckIn.getLatitude(),
                    lastCheckIn.getLongitude(),
                    request.getLatitude(),
                    request.getLongitude()
            );
        }

        CheckIn checkIn = checkInRepository.save(CheckIn.builder()
                .agent(agent)
                .type("CHECKIN")
                .source("MANUAL")
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .notes(request.getNotes())
                .distanceFromPrevious(distance)
                .occurredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build());
        realtimeEventService.publish("check-in.created", "check-ins");
        return checkIn;
    }

    public List<CheckIn> findByAgent(Long agentId) {
        return checkInRepository.findByAgentIdOrderByOccurredAtDesc(agentId);
    }

    public List<CheckIn> findAll() {
        return checkInRepository.findAll();
    }
}
