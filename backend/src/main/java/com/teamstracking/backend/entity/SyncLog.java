package com.teamstracking.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Table(name = "sync_logs")
@Entity(name = "SyncLog")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduler_name")
    private String schedulerName;

    private String status;

    @Column(name = "records_processed")
    private Integer recordsProcessed = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "sync_token")
    private String syncToken;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;
}
