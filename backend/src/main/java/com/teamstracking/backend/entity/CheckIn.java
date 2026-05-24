package com.teamstracking.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "check_ins")
@Entity(name = "CheckIn")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_event_id", unique = true)
    private String externalEventId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    private String type;
    private String source = "MANUAL";
    private Double latitude;
    private Double longitude;
    private String address;
    private Double accuracy;
    private Double speed;
    private String notes;

    @Column(name = "distance_from_previous")
    private Double distanceFromPrevious;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}