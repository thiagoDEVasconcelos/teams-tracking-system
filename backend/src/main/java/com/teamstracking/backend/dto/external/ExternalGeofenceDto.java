package com.teamstracking.backend.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalGeofenceDto {
    private String id;
    private String name;
    private String type;
    private Object coordinates;
    private Boolean alertOnEnter;
    private Boolean alertOnExit;
    private Object teams;
}