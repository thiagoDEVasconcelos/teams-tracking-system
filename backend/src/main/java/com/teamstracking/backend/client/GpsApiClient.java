package com.teamstracking.backend.client;

import com.teamstracking.backend.dto.external.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class GpsApiClient {

    private final WebClient webClient;

    public GpsApiClient(@Value("${gps.api.base-url}") String baseUrl,
                        @Value("${gps.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    public List<ExternalAgentDto> fetchAgents() {
        try {
            ExternalAgentResponse response = webClient.get()
                    .uri("/api/v1/agents/")
                    .retrieve()
                    .bodyToMono(ExternalAgentResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        log.error("Erro ao buscar agentes da API externa: {}", e.getMessage());
                        return Mono.just(new ExternalAgentResponse());
                    })
                    .block();

            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }
            return response.getData();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar agentes: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ExternalLocationDto> fetchLocations() {
        try {
            ExternalLocationResponse response = webClient.get()
                    .uri("/api/v1/locations/")
                    .retrieve()
                    .bodyToMono(ExternalLocationResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        log.error("Erro ao buscar localizações: {}", e.getMessage());
                        return Mono.just(new ExternalLocationResponse());
                    })
                    .block();

            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }
            return response.getData();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar localizações: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public ExternalCheckInResponse fetchCheckIns(String syncToken) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/api/v1/check-ins/");
                        if (syncToken != null) {
                            builder = builder.queryParam("syncToken", syncToken);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(ExternalCheckInResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        log.error("Erro ao buscar check-ins: {}", e.getMessage());
                        return Mono.just(new ExternalCheckInResponse());
                    })
                    .block();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar check-ins: {}", e.getMessage());
            return new ExternalCheckInResponse();
        }
    }

    public List<ExternalGeofenceDto> fetchGeofences() {
        try {
            ExternalGeofenceResponse response = webClient.get()
                    .uri("/api/v1/geofences/")
                    .retrieve()
                    .bodyToMono(ExternalGeofenceResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        log.error("Erro ao buscar geofences: {}", e.getMessage());
                        return Mono.just(new ExternalGeofenceResponse());
                    })
                    .block();

            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }
            return response.getData();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar geofences: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}