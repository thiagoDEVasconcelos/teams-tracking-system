package com.teamstracking.backend.scheduler;

import com.teamstracking.backend.client.GpsApiClient;
import com.teamstracking.backend.dto.external.ExternalGeofenceDto;
import com.teamstracking.backend.entity.Geofence;
import com.teamstracking.backend.entity.SyncLog;
import com.teamstracking.backend.repository.GeofenceRepository;
import com.teamstracking.backend.repository.SyncLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeofenceSyncScheduler {

    private final GpsApiClient gpsApiClient;
    private final GeofenceRepository geofenceRepository;
    private final SyncLogRepository syncLogRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 600000)
    public void syncGeofences() {
        log.info("Iniciando sincronização de geofences...");
        int count = 0;
        String errorMessage = null;

        try {
            List<ExternalGeofenceDto> geofences = gpsApiClient.fetchGeofences();

            for (ExternalGeofenceDto dto : geofences) {
                Geofence geofence = geofenceRepository.findByExternalId(dto.getId())
                        .orElse(new Geofence());

                geofence.setExternalId(dto.getId());
                geofence.setName(dto.getName());
                geofence.setType(dto.getType());
                geofence.setAlertOnEnter(dto.getAlertOnEnter());
                geofence.setAlertOnExit(dto.getAlertOnExit());

                try {
                    if (dto.getCoordinates() != null) {
                        geofence.setCoordinates(objectMapper.writeValueAsString(dto.getCoordinates()));
                    }
                    if (dto.getTeams() != null) {
                        geofence.setTeams(objectMapper.writeValueAsString(dto.getTeams()));
                    }
                } catch (Exception e) {
                    log.warn("Erro ao serializar coordenadas da geofence {}: {}", dto.getId(), e.getMessage());
                }

                geofence.setUpdatedAt(LocalDateTime.now());
                if (geofence.getId() == null) {
                    geofence.setCreatedAt(LocalDateTime.now());
                }

                geofenceRepository.save(geofence);
                count++;
            }

            log.info("Sincronização de geofences concluída: {} geofences processadas", count);

        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro na sincronização de geofences: {}", e.getMessage());
        }

        syncLogRepository.save(SyncLog.builder()
                .schedulerName("GEOFENCE_SYNC")
                .status(errorMessage == null ? "SUCCESS" : "ERROR")
                .recordsProcessed(count)
                .errorMessage(errorMessage)
                .executedAt(LocalDateTime.now())
                .build());
    }
}