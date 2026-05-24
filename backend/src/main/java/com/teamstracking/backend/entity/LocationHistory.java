package com.teamstracking.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "location_history")
@Entity(name = "LocationHistory")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double speed;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}