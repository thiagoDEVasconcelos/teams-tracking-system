package com.teamstracking.backend.scheduler;

import com.teamstracking.backend.client.GpsApiClient;
import com.teamstracking.backend.dto.external.ExternalCheckInDto;
import com.teamstracking.backend.dto.external.ExternalCheckInResponse;
import com.teamstracking.backend.entity.Agent;
import com.teamstracking.backend.entity.CheckIn;
import com.teamstracking.backend.entity.SyncLog;
import com.teamstracking.backend.repository.AgentRepository;
import com.teamstracking.backend.repository.CheckInRepository;
import com.teamstracking.backend.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckInSyncScheduler {

    private final GpsApiClient gpsApiClient;
    private final CheckInRepository checkInRepository;
    private final AgentRepository agentRepository;
    private final SyncLogRepository syncLogRepository;

    @Scheduled(fixedDelay = 60000)
    public void syncCheckIns() {
        log.info("Iniciando sincronização de check-ins...");
        int count = 0;
        String errorMessage = null;
        String newSyncToken = null;

        try {
            String lastToken = syncLogRepository
                    .findTopBySchedulerNameAndStatusOrderByExecutedAtDesc("CHECKIN_SYNC", "SUCCESS")
                    .map(SyncLog::getSyncToken)
                    .orElse(null);

            log.info("Usando syncToken: {}", lastToken != null ? lastToken : "nenhum (primeira execução)");

            ExternalCheckInResponse response = gpsApiClient.fetchCheckIns(lastToken);

            if (response != null && response.getData() != null) {
                for (ExternalCheckInDto dto : response.getData()) {
                    if (checkInRepository.findByExternalEventId(dto.getId()).isPresent()) {
                        continue;
                    }

                    Optional<Agent> agentOpt = agentRepository.findByExternalId(dto.getAgentId());
                    if (agentOpt.isEmpty()) continue;

                    checkInRepository.save(CheckIn.builder()
                            .externalEventId(dto.getId())
                            .agent(agentOpt.get())
                            .type(dto.getType())
                            .source("AUTO")
                            .latitude(dto.getLatitude())
                            .longitude(dto.getLongitude())
                            .address(dto.getAddress())
                            .accuracy(dto.getAccuracy())
                            .speed(dto.getSpeed())
                            .notes(dto.getNotes())
                            .distanceFromPrevious(dto.getDistanceFromPrevious())
                            .occurredAt(LocalDateTime.now())
                            .syncedAt(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build());
                    count++;
                }
                newSyncToken = response.getSyncToken();
            }

            log.info("Sincronização de check-ins concluída: {} novos eventos", count);

        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro na sincronização de check-ins: {}", e.getMessage());
        }

        syncLogRepository.save(SyncLog.builder()
                .schedulerName("CHECKIN_SYNC")
                .status(errorMessage == null ? "SUCCESS" : "ERROR")
                .recordsProcessed(count)
                .errorMessage(errorMessage)
                .syncToken(newSyncToken)
                .executedAt(LocalDateTime.now())
                .build());
    }
}