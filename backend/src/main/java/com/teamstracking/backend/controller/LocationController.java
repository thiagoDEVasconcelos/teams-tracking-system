package com.teamstracking.backend.controller;

import com.teamstracking.backend.entity.LocationHistory;
import com.teamstracking.backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/agent/{agentId}/route")
    public ResponseEntity<List<LocationHistory>> getAgentRoute(
            @PathVariable Long agentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(locationService.getAgentRoutByDate(agentId, date));
    }
}