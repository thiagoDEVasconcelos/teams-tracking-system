package com.teamstracking.backend.service;

import com.teamstracking.backend.client.GpsApiClient;
import com.teamstracking.backend.entity.*;
import com.teamstracking.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final GpsApiClient gpsApiClient;
    private final AgentRepository agentRepository;
    private final LocationHistoryRepository locationHistoryRepository;
    private final CheckInRepository checkInRepository;
    private final GeofenceRepository geofenceRepository;
    private final SyncLogRepository syncLogRepository;

    private static final double MAX_ACCURACY_METERS = 50.0;

    public int syncAgents() {
        int count = 0;
        String errorMessage = null;
        try {
            var agents = gpsApiClient.fetchAgents();
            for (var dto : agents) {
                Agent agent = agentRepository.findByExternalId(dto.getId())
                        .orElse(new Agent());
                agent.setExternalId(dto.getId());
                agent.setName(dto.getName());
                agent.setRole(dto.getRole());
                agent.setTeam(dto.getTeam());
                agent.setPhone(dto.getPhone());
                agent.setEmail(dto.getEmail());
                agent.setActive(dto.getActive());
                agent.setStatus(dto.getStatus() != null ? dto.getStatus() : "OFFLINE");
                agent.setBattery(dto.getBattery() != null ? dto.getBattery() : 0);
                agent.setUpdatedAt(LocalDateTime.now());
                if (agent.getId() == null) agent.setCreatedAt(LocalDateTime.now());
                agentRepository.save(agent);
                count++;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro ao sincronizar agentes: {}", e.getMessage());
        }
        saveSyncLog("AGENT_SYNC", count, errorMessage, null);
        return count;
    }

    public int syncLocations() {
        int count = 0;
        String errorMessage = null;
        try {
            var locations = gpsApiClient.fetchLocations();
            for (var dto : locations) {
                if (dto.getAccuracy() != null && dto.getAccuracy() > MAX_ACCURACY_METERS) continue;

                Optional<Agent> agentOpt = agentRepository.findByExternalId(dto.getAgentId());
                if (agentOpt.isEmpty()) continue;

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
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro ao sincronizar localizações: {}", e.getMessage());
        }
        saveSyncLog("LOCATION_SYNC", count, errorMessage, null);
        return count;
    }

    public int syncCheckIns() {
        int count = 0;
        String errorMessage = null;
        String newSyncToken = null;
        try {
            String lastToken = syncLogRepository
                    .findTopBySchedulerNameAndStatusOrderByExecutedAtDesc("CHECKIN_SYNC", "SUCCESS")
                    .map(SyncLog::getSyncToken)
                    .orElse(null);

            var response = gpsApiClient.fetchCheckIns(lastToken);
            if (response != null && response.getData() != null) {
                for (var dto : response.getData()) {
                    if (checkInRepository.findByExternalEventId(dto.getId()).isPresent()) continue;
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
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro ao sincronizar check-ins: {}", e.getMessage());
        }
        saveSyncLog("CHECKIN_SYNC", count, errorMessage, newSyncToken);
        return count;
    }

    public int syncGeofences() {
        int count = 0;
        String errorMessage = null;
        try {
            var geofences = gpsApiClient.fetchGeofences();
            for (var dto : geofences) {
                Geofence geofence = geofenceRepository.findByExternalId(dto.getId())
                        .orElse(new Geofence());
                geofence.setExternalId(dto.getId());
                geofence.setName(dto.getName());
                geofence.setType(dto.getType());
                geofence.setAlertOnEnter(dto.getAlertOnEnter());
                geofence.setAlertOnExit(dto.getAlertOnExit());
                geofence.setUpdatedAt(LocalDateTime.now());
                if (geofence.getId() == null) geofence.setCreatedAt(LocalDateTime.now());
                geofenceRepository.save(geofence);
                count++;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Erro ao sincronizar geofences: {}", e.getMessage());
        }
        saveSyncLog("GEOFENCE_SYNC", count, errorMessage, null);
        return count;
    }

    private void saveSyncLog(String schedulerName, int count, String errorMessage, String syncToken) {
        syncLogRepository.save(SyncLog.builder()
                .schedulerName(schedulerName)
                .status(errorMessage == null ? "SUCCESS" : "ERROR")
                .recordsProcessed(count)
                .errorMessage(errorMessage)
                .syncToken(syncToken)
                .executedAt(LocalDateTime.now())
                .build());
    }
}