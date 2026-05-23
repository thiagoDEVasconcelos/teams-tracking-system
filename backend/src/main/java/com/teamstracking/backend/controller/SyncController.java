package com.teamstracking.backend.controller;

import com.teamstracking.backend.entity.SyncLog;
import com.teamstracking.backend.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sync-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SyncController {

    private final SyncLogRepository syncLogRepository;

    @GetMapping
    public ResponseEntity<List<SyncLog>> findAll() {
        return ResponseEntity.ok(syncLogRepository.findAll());
    }

    @GetMapping("/{schedulerName}")
    public ResponseEntity<List<SyncLog>> findByScheduler(@PathVariable String schedulerName) {
        return ResponseEntity.ok(
                syncLogRepository.findAll().stream()
                        .filter(log -> log.getSchedulerName().equals(schedulerName))
                        .toList()
        );
    }
}