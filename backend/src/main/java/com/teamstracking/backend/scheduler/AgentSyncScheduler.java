package com.teamstracking.backend.scheduler;

import com.teamstracking.backend.client.GpsApiClient;
import com.teamstracking.backend.dto.external.ExternalAgentDto;
import com.teamstracking.backend.entity.Agent;
import com.teamstracking.backend.entity.SyncLog;
import com.teamstracking.backend.repository.AgentRepository;
import com.teamstracking.backend.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentSyncScheduler {

    private final GpsApiClient gpsApiClient;
    private final AgentRepository agentRepository;
    private final SyncLogRepository syncLogRepository;

    @Scheduled(fixedDelay = 300000)
    public void syncAgents() {
        log.info("Iniciando sincronização de agentes...");
        int count = 0;
        String errorMessage = null;

        try {
            List<ExternalAgentDto> externalAgents = gpsApiClient.fetchAgents();

            for (ExternalAgentDto dto : externalAgents) {
                Agent agent = agentRepository.findByExternalId(dto.getId())
                        .orElse(new Agent());

                agent.setExternalId((dto.getId()));
                agent.setName(dto.getName());
                agent.setRole(dto.getRole());
                agent.setTeam(dto.getTeam());
                agent.setPhone(dto.getPhone());
                agent.setEmail(dto.getEmail());
                agent.setActive(dto.getActive());
                agent.setStatus(dto.getStatus() != null ? dto.getStatus() : "OFFLINE");
                agent.setBattery(dto.getBattery() != null ? dto.getBattery() : 0);
                agent.setUpdatedAt(LocalDateTime.now());

                if (agent.getId() == null) {
                    agent.setCreatedAt(LocalDateTime.now());
                }

                agentRepository.save(agent);
                count++;
            }

            log.info("Sincronização de agentes concluída: {} agentes processados", count);

        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro na sincronização de agentes: {}", e.getMessage());
        }

        // Salva o log da execução
        syncLogRepository.save(SyncLog.builder()
                .schedulerName("AGENT_SYNC")
                .status(errorMessage == null ? "SUCCESS" : "ERROR")
                .recordsProcessed(count)
                .errorMessage(errorMessage)
                .executedAt(LocalDateTime.now())
                .build());
    }
}