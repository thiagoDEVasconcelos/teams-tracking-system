package com.teamstracking.backend.controller;

import com.teamstracking.backend.entity.Geofence;
import com.teamstracking.backend.repository.GeofenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/geofences")
@RequiredArgsConstructor
public class GeofenceController {

    private final GeofenceRepository geofenceRepository;

    @GetMapping
    public ResponseEntity<List<Geofence>> findAll() {
        return ResponseEntity.ok(geofenceRepository.findAll());
    }
}