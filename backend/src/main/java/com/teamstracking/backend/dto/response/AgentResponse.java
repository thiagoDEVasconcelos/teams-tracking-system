package com.teamstracking.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AgentResponse {
    private Long id;
    private String externalId;
    private String name;
    private String role;
    private String team;
    private String phone;
    private String email;
    private Boolean active;
    private String status;
    private Integer battery;
    private Double latitude;
    private Double longitude;
    private String currentAddress;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
}