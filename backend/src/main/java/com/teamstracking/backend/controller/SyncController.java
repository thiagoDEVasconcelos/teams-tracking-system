package com.teamstracking.backend.controller;

import com.teamstracking.backend.repository.SyncLogRepository;
import com.teamstracking.backend.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SyncController {

    private final SyncService syncService;
    private final SyncLogRepository syncLogRepository;

    @PostMapping("/agents")
    public ResponseEntity<?> syncAgents() {
        return ResponseEntity.ok(Map.of("synced", syncService.syncAgents()));
    }

    @PostMapping("/locations")
    public ResponseEntity<?> syncLocations() {
        return ResponseEntity.ok(Map.of("synced", syncService.syncLocations()));
    }

    @PostMapping("/check-ins")
    public ResponseEntity<?> syncCheckIns() {
        return ResponseEntity.ok(Map.of("synced", syncService.syncCheckIns()));
    }

    @PostMapping("/geofences")
    public ResponseEntity<?> syncGeofences() {
        return ResponseEntity.ok(Map.of("synced", syncService.syncGeofences()));
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs() {
        return ResponseEntity.ok(syncLogRepository.findAll());
    }
}