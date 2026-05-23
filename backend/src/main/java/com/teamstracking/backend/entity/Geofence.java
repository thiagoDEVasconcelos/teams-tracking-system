package com.teamstracking.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "geofences")
@Entity(name = "Geofence")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Geofence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true)
    private String externalId;

    private String name;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String coordinates;

    @Column(name = "alert_on_enter")
    private Boolean alertOnEnter = false;

    @Column(name = "alert_on_exit")
    private Boolean alertOnExit = false;

    private String teams;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}