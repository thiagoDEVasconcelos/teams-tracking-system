package com.teamstracking.backend.service;

import com.teamstracking.backend.dto.request.AgentRequest;
import com.teamstracking.backend.dto.response.AgentResponse;
import com.teamstracking.backend.entity.Agent;
import com.teamstracking.backend.repository.AgentRepository;
import jakarta.persistence.EntityNotFoundException;
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
class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private RealtimeEventService realtimeEventService;

    @InjectMocks
    private AgentService agentService;

    @Test
    void deveRetornarListaDeAgentes() {
        Agent agent = new Agent();
        agent.setId(1L);
        agent.setName("Thiago Sousa");
        agent.setEmail("thiago@test.com");
        agent.setStatus("ONLINE");
        agent.setBattery(80);
        agent.setActive(true);

        when(agentRepository.findAll()).thenReturn(List.of(agent));

        List<AgentResponse> result = agentService.findAll();

        assertEquals(1, result.size());
        assertEquals("Thiago Sousa", result.get(0).getName());
    }

    @Test
    void deveLancarExcecaoQuandoAgentaNaoEncontrado() {
        when(agentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> agentService.findById(99L));
    }

    @Test
    void deveCriarAgenteComSucesso() {
        AgentRequest request = new AgentRequest();
        request.setName("Maria Monroe");
        request.setEmail("mariamonroe@test.com");
        request.setRole("TECHNICIAN");
        request.setTeam("Alpha");
        request.setPhone("453453453453");

        Agent agentSalvo = new Agent();
        agentSalvo.setId(1L);
        agentSalvo.setName("Maria Monroe");
        agentSalvo.setEmail("mariamonroe@test.com");
        agentSalvo.setStatus("OFFLINE");
        agentSalvo.setBattery(0);
        agentSalvo.setActive(true);

        when(agentRepository.save(any(Agent.class))).thenReturn(agentSalvo);

        AgentResponse result = agentService.create(request);

        assertEquals("Maria Monroe", result.getName());
        verify(agentRepository, times(1)).save(any(Agent.class));
        verify(realtimeEventService).publish("agent.created", "agents");
    }

    @Test
    void deveDeletarAgenteExistente() {
        Agent agent = new Agent();
        agent.setId(1L);
        agent.setName("João Maria");

        when(agentRepository.findById(1L)).thenReturn(Optional.of(agent));

        agentService.delete(1L);

        verify(agentRepository, times(1)).delete(agent);
        verify(realtimeEventService).publish("agent.deleted", "agents");
    }
}
