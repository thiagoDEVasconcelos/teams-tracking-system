package com.teamstracking.backend.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ExternalLocationResponse {
    private List<ExternalLocationDto> data;
}