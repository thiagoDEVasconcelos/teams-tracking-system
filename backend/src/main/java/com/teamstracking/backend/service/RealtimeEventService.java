package com.teamstracking.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class RealtimeEventService {

    private static final long TIMEOUT = 0L;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(error -> emitters.remove(emitter));

        emitters.add(emitter);
        send(emitter, "connected", "system");

        return emitter;
    }

    public void publish(String type, String resource) {
        for (SseEmitter emitter : emitters) {
            send(emitter, type, resource);
        }
    }

    private void send(SseEmitter emitter, String type, String resource) {
        try {
            emitter.send(SseEmitter.event()
                    .name("realtime")
                    .data(Map.of(
                            "type", type,
                            "resource", resource,
                            "occurredAt", LocalDateTime.now().toString()
                    )));
        } catch (IOException | IllegalStateException e) {
            emitters.remove(emitter);
            log.debug("Removed closed realtime SSE emitter: {}", e.getMessage());
        }
    }
}
