package com.teamstracking.backend.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ExternalGeofenceResponse {
    private List<ExternalGeofenceDto> data;
}