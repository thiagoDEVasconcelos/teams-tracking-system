package com.teamstracking.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckInRequest {

    @NotNull(message = "ID do agente é obrigatório")
    private Long agentId;

    @NotNull(message = "Latitude é obrigatória")
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    private Double longitude;

    private String notes;
    private String address;
}