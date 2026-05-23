package com.teamstracking.backend.scheduler;

import com.teamstracking.backend.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckInSyncScheduler {

    private final SyncService syncService;

    @Scheduled(fixedDelay = 60000)
    public void syncCheckIns() {
        log.info("Scheduler: sincronizando check-ins...");
        syncService.syncCheckIns();
    }
}