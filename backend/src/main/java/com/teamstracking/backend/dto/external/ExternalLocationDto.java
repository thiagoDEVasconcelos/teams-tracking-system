package com.teamstracking.backend.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalLocationDto {
    private String agentId;
    private String externalId;
    private String name;
    private Double latitude;
    private Double longitude;
    private String currentAddress;
    private Double accuracy;
    private Double speed;
    private Integer battery;
    private String status;
    private String lastSeen;
}