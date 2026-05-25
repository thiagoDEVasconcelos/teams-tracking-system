package com.teamstracking.backend.client;

import com.teamstracking.backend.dto.external.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.ArrayList;
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
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(60))
                ))
                .build();
    }

    private <T> Mono<T> withResilience(Mono<T> mono) {
        return mono
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(30))
                        .jitter(0.5)
                        .filter(throwable -> {
                            if (throwable instanceof WebClientResponseException ex) {
                                if (ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                                    log.warn("API externa retornou 503, aplicando backoff...");
                                    return true;
                                }
                                if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                    String retryAfter = ex.getHeaders().getFirst("Retry-After");
                                    long waitSeconds = retryAfter != null ? Long.parseLong(retryAfter) : 10;
                                    log.warn("API externa retornou 429, aguardando {}s...", waitSeconds);
                                    Mono.delay(Duration.ofSeconds(waitSeconds)).block();
                                    return true;
                                }
                            }
                            return false;
                        })
                );
    }

    @CircuitBreaker(name = "gpsApi", fallbackMethod = "fetchAgentsFallback")
    public List<ExternalAgentDto> fetchAgents() {
        List<ExternalAgentDto> allAgents = new ArrayList<>();
        int page = 1;
        boolean hasMore = true;

        while (hasMore) {
            final int currentPage = page;
            try {
                ExternalAgentResponse response = withResilience(
                        webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/api/v1/agents/")
                                        .queryParam("page", currentPage)
                                        .queryParam("limit", 50)
                                        .build())
                                .retrieve()
                                .bodyToMono(ExternalAgentResponse.class)
                                .timeout(Duration.ofSeconds(60))
                )
                        .onErrorResume(e -> {
                            log.error("Erro ao buscar agentes página {}: {}", currentPage, e.getMessage());
                            return Mono.just(new ExternalAgentResponse());
                        })
                        .block();

                if (response == null || response.getData() == null || response.getData().isEmpty()) {
                    hasMore = false;
                } else {
                    allAgents.addAll(response.getData());
                    hasMore = response.getData().size() == 50;
                    page++;
                }
            } catch (Exception e) {
                log.error("Erro inesperado na paginação de agentes: {}", e.getMessage());
                hasMore = false;
            }
        }

        log.info("Total de agentes buscados: {}", allAgents.size());
        return allAgents;
    }

    public List<ExternalAgentDto> fetchAgentsFallback(Exception e) {
        log.warn("Circuit Breaker ativo para fetchAgents: {}", e.getMessage());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "gpsApi", fallbackMethod = "fetchLocationsFallback")
    public List<ExternalLocationDto> fetchLocations() {
        try {
            ExternalLocationResponse response = withResilience(
                    webClient.get()
                            .uri("/api/v1/locations/")
                            .retrieve()
                            .bodyToMono(ExternalLocationResponse.class)
                            .timeout(Duration.ofSeconds(60))
            )
                    .onErrorResume(e -> {
                        log.error("Erro ao buscar localizações após retries: {}", e.getMessage());
                        return Mono.just(new ExternalLocationResponse());
                    })
                    .block();

            if (response == null || response.getData() == null) return Collections.emptyList();
            return response.getData();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar localizações: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ExternalLocationDto> fetchLocationsFallback(Exception e) {
        log.warn("Circuit Breaker ativo para fetchLocations: {}", e.getMessage());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "gpsApi", fallbackMethod = "fetchCheckInsFallback")
    public ExternalCheckInResponse fetchCheckIns(String syncToken) {
        try {
            return withResilience(
                    webClient.get()
                            .uri(uriBuilder -> {
                                var builder = uriBuilder.path("/api/v1/check-ins/");
                                if (syncToken != null) builder = builder.queryParam("syncToken", syncToken);
                                return builder.build();
                            })
                            .retrieve()
                            .bodyToMono(ExternalCheckInResponse.class)
                            .timeout(Duration.ofSeconds(60))
            )
                    .onErrorResume(e -> {
                        log.error("Erro ao buscar check-ins após retries: {}", e.getMessage());
                        return Mono.just(new ExternalCheckInResponse());
                    })
                    .block();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar check-ins: {}", e.getMessage());
            return new ExternalCheckInResponse();
        }
    }

    public ExternalCheckInResponse fetchCheckInsFallback(Exception e) {
        log.warn("Circuit Breaker ativo para fetchCheckIns: {}", e.getMessage());
        return new ExternalCheckInResponse();
    }

    @CircuitBreaker(name = "gpsApi", fallbackMethod = "fetchGeofencesFallback")
    public List<ExternalGeofenceDto> fetchGeofences() {
        try {
            ExternalGeofenceResponse response = withResilience(
                    webClient.get()
                            .uri("/api/v1/geofences/")
                            .retrieve()
                            .bodyToMono(ExternalGeofenceResponse.class)
                            .timeout(Duration.ofSeconds(60))
            )
                    .onErrorResume(e -> {
                        log.error("Erro ao buscar geofences após retries: {}", e.getMessage());
                        return Mono.just(new ExternalGeofenceResponse());
                    })
                    .block();

            if (response == null || response.getData() == null) return Collections.emptyList();
            return response.getData();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar geofences: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ExternalGeofenceDto> fetchGeofencesFallback(Exception e) {
        log.warn("Circuit Breaker ativo para fetchGeofences: {}", e.getMessage());
        return Collections.emptyList();
    }
}