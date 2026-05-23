package com.teamstracking.backend.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalAgentDto {
    private String id;
    private String externalId;
    private String name;
    private String role;
    private String team;
    private String phone;
    private String email;
    private Boolean active;
    private String status;
    private Integer battery;
    private String lastSeen;
    private String createdAt;
    private String updatedAt;
}