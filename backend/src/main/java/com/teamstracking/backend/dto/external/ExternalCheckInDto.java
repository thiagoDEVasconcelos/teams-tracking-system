package com.teamstracking.backend.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalCheckInDto {
    private String id;
    private String agentId;
    private String type;
    private String source;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double accuracy;
    private Double speed;
    private String notes;
    private Double distanceFromPrevious;
    private String externalEventId;
    private String occurredAt;
    private String syncedAt;
}