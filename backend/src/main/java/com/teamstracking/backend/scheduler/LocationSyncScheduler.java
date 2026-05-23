package com.teamstracking.backend.scheduler;

import com.teamstracking.backend.client.GpsApiClient;
import com.teamstracking.backend.dto.external.ExternalLocationDto;
import com.teamstracking.backend.entity.Agent;
import com.teamstracking.backend.entity.LocationHistory;
import com.teamstracking.backend.entity.SyncLog;
import com.teamstracking.backend.repository.AgentRepository;
import com.teamstracking.backend.repository.LocationHistoryRepository;
import com.teamstracking.backend.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationSyncScheduler {

    private final GpsApiClient gpsApiClient;
    private final AgentRepository agentRepository;
    private final LocationHistoryRepository locationHistoryRepository;
    private final SyncLogRepository syncLogRepository;

    private static final double MAX_ACCURACY_METERS = 50.0;

    @Scheduled(fixedDelay = 30000)
    public void syncLocations() {
        log.info("Iniciando sincronização de localizações...");
        int count = 0;
        String errorMessage = null;

        try {
            List<ExternalLocationDto> locations = gpsApiClient.fetchLocations();

            for (ExternalLocationDto dto : locations) {

                if (dto.getAccuracy() != null && dto.getAccuracy() > MAX_ACCURACY_METERS) {
                    log.warn("Localização descartada por acurácia: {}m para agente {}", dto.getAccuracy(), dto.getAgentId());
                    continue;
                }

                Optional<Agent> agentOpt = agentRepository.findByExternalId(dto.getAgentId());
                if (agentOpt.isEmpty()) {
                    log.warn("Agente não encontrado para externalId: {}", dto.getAgentId());
                    continue;
                }

                Agent agent = agentOpt.get();

                agent.setLatitude(dto.getLatitude());
                agent.setLongitude(dto.getLongitude());
                agent.setCurrentAddress(dto.getCurrentAddress());
                agent.setAccuracy(dto.getAccuracy());
                agent.setSpeed(dto.getSpeed());
                agent.setBattery(dto.getBattery() != null ? dto.getBattery() : agent.getBattery());
                agent.setStatus(dto.getStatus() != null ? dto.getStatus() : agent.getStatus());
                agent.setLastSeen(LocalDateTime.now());
                agent.setUpdatedAt(LocalDateTime.now());
                agentRepository.save(agent);

                locationHistoryRepository.save(LocationHistory.builder()
                        .agent(agent)
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .accuracy(dto.getAccuracy())
                        .speed(dto.getSpeed())
                        .recordedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build());

                count++;
            }

            log.info("Sincronização de localizações concluída: {} pontos processados", count);

        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro na sincronização de localizações: {}", e.getMessage());
        }

        syncLogRepository.save(SyncLog.builder()
                .schedulerName("LOCATION_SYNC")
                .status(errorMessage == null ? "SUCCESS" : "ERROR")
                .recordsProcessed(count)
                .errorMessage(errorMessage)
                .executedAt(LocalDateTime.now())
                .build());
    }
}