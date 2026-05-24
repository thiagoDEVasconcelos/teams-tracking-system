package com.teamstracking.backend.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalGeofenceDto {
    private String id;
    private String name;
    private String type;
    private String coordinatesJson;
    private Boolean alertOnEnter;
    private Boolean alertOnExit;
    private String assignedTeams;
}