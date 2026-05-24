package com.teamstracking.backend.repository;

import com.teamstracking.backend.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    Optional<CheckIn> findByExternalEventId(String externalEventId);
    List<CheckIn> findByAgentIdOrderByOccurredAtDesc(Long agentId);
    Optional<CheckIn> findTopByAgentIdOrderByOccurredAtDesc(Long agentId);
}