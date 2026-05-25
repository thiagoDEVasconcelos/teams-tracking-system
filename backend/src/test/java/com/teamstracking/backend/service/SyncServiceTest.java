package com.teamstracking.backend.service;

import com.teamstracking.backend.client.GpsApiClient;
import com.teamstracking.backend.dto.external.ExternalAgentDto;
import com.teamstracking.backend.entity.Agent;
import com.teamstracking.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @Mock private GpsApiClient gpsApiClient;
    @Mock private AgentRepository agentRepository;
    @Mock private LocationHistoryRepository locationHistoryRepository;
    @Mock private CheckInRepository checkInRepository;
    @Mock private GeofenceRepository geofenceRepository;
    @Mock private SyncLogRepository syncLogRepository;
    @Mock private RealtimeEventService realtimeEventService;

    @InjectMocks
    private SyncService syncService;

    @Test
    void deveSincronizarAgentesComSucesso() {
        ExternalAgentDto dto = new ExternalAgentDto();
        dto.setId("ext-001");
        dto.setName("Ana Rodrigues");
        dto.setEmail("ana@media4all.com");
        dto.setActive(true);
        dto.setStatus("ONLINE");
        dto.setBattery(80);

        when(gpsApiClient.fetchAgents()).thenReturn(List.of(dto));
        when(agentRepository.findByExternalId("ext-001")).thenReturn(Optional.empty());
        when(agentRepository.save(any(Agent.class))).thenReturn(new Agent());
        when(syncLogRepository.save(any())).thenReturn(null);

        int result = syncService.syncAgents();

        assertEquals(1, result);
        verify(agentRepository, times(1)).save(any(Agent.class));
        verify(realtimeEventService).publish("sync-log.created", "sync-logs");
        verify(realtimeEventService).publish("agents.synced", "agents");
    }

    @Test
    void deveRetornarZeroQuandoApiExternaFalhar() {
        when(gpsApiClient.fetchAgents()).thenReturn(List.of());
        when(syncLogRepository.save(any())).thenReturn(null);

        int result = syncService.syncAgents();

        assertEquals(0, result);
        verify(realtimeEventService).publish("sync-log.created", "sync-logs");
        verify(realtimeEventService, never()).publish("agents.synced", "agents");
    }
}
