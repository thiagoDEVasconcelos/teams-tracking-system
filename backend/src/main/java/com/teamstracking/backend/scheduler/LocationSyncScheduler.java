package com.teamstracking.backend.scheduler;

import com.teamstracking.backend.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationSyncScheduler {

    private final SyncService syncService;

    @Scheduled(fixedDelay = 30000)
    public void syncLocations() {
        log.info("Scheduler: sincronizando localizações...");
        syncService.syncLocations();
    }
}