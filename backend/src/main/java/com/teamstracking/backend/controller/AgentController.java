package com.teamstracking.backend.controller;

import com.teamstracking.backend.dto.request.AgentRequest;
import com.teamstracking.backend.dto.response.AgentResponse;
import com.teamstracking.backend.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public ResponseEntity<List<AgentResponse>> findAll() {
        return ResponseEntity.ok(agentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AgentResponse> create(@RequestBody @Valid AgentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentResponse> update(@PathVariable Long id, @RequestBody @Valid AgentRequest request) {
        return ResponseEntity.ok(agentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}