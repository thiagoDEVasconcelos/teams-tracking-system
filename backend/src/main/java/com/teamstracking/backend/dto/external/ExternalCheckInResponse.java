package com.teamstracking.backend.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ExternalCheckInResponse {
    private List<ExternalCheckInDto> data;
    private String syncToken;
}