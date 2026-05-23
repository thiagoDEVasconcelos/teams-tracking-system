package com.teamstracking.backend.service;

import com.teamstracking.backend.dto.request.AgentRequest;
import com.teamstracking.backend.dto.response.AgentResponse;
import com.teamstracking.backend.entity.Agent;
import com.teamstracking.backend.repository.AgentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    public List<AgentResponse> findAll() {
        return agentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AgentResponse findById(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado: " + id));
        return toResponse(agent);
    }

    public AgentResponse create(AgentRequest request) {
        Agent agent = Agent.builder()
                .name(request.getName())
                .role(request.getRole())
                .team(request.getTeam())
                .phone(request.getPhone())
                .email(request.getEmail())
                .active(true)
                .status("OFFLINE")
                .battery(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(agentRepository.save(agent));
    }

    public AgentResponse update(Long id, AgentRequest request) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado: " + id));
        agent.setName(request.getName());
        agent.setRole(request.getRole());
        agent.setTeam(request.getTeam());
        agent.setPhone(request.getPhone());
        agent.setEmail(request.getEmail());
        agent.setUpdatedAt(LocalDateTime.now());
        return toResponse(agentRepository.save(agent));
    }

    public void delete(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado: " + id));
        agentRepository.delete(agent);
    }

    private AgentResponse toResponse(Agent agent) {
        return AgentResponse.builder()
                .id(agent.getId())
                .externalId(agent.getExternalId())
                .name(agent.getName())
                .role(agent.getRole())
                .team(agent.getTeam())
                .phone(agent.getPhone())
                .email(agent.getEmail())
                .active(agent.getActive())
                .status(agent.getStatus())
                .battery(agent.getBattery())
                .latitude(agent.getLatitude())
                .longitude(agent.getLongitude())
                .currentAddress(agent.getCurrentAddress())
                .lastSeen(agent.getLastSeen())
                .createdAt(agent.getCreatedAt())
                .build();
    }
}