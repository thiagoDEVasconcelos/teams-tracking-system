package com.teamstracking.backend.controller;

import com.teamstracking.backend.dto.request.CheckInRequest;
import com.teamstracking.backend.entity.CheckIn;
import com.teamstracking.backend.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/check-ins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping
    public ResponseEntity<List<CheckIn>> findAll() {
        return ResponseEntity.ok(checkInService.findAll());
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<CheckIn>> findByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(checkInService.findByAgent(agentId));
    }

    @PostMapping
    public ResponseEntity<CheckIn> create(@RequestBody @Valid CheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(checkInService.createManual(request));
    }
}