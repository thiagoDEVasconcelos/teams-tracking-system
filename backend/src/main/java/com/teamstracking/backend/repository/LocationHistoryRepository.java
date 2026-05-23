package com.teamstracking.backend.repository;

import com.teamstracking.backend.entity.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationHistoryRepository extends JpaRepository<LocationHistory, Long> {
    List<LocationHistory> findByAgentIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long agentId,
            LocalDateTime start,
            LocalDateTime end
    );
}