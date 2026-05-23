package com.teamstracking.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "agents")
@Entity(name = "Agent")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true)
    private Long externalId;

    private String name;
    private String role;
    private String team;
    private String phone;
    private String email;
    private Boolean active = true;
    private String status = "OFFLINE";
    private Integer battery = 0;
    private Double latitude;
    private Double longitude;

    @Column(name = "current_address")
    private String currentAddress;

    private Double accuracy;
    private Double speed;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
