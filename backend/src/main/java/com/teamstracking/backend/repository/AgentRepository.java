package com.teamstracking.backend.repository;

import com.teamstracking.backend.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByExternalId(String externalId);
    List<Agent> findByActiveTrue();
    List<Agent> findByStatus(String status);
}