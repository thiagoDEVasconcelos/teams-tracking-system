package com.teamstracking.backend.repository;

import com.teamstracking.backend.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {

    Optional<SyncLog> findTopBySchedulerNameAndStatusOrderByExecutedAtDesc(
            String schedulerName,
            String status
    );
}