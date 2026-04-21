package com.wafa.assurance.repository;

import com.wafa.assurance.model.MissionTransition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionTransitionRepository extends JpaRepository<MissionTransition, Long> {
    List<MissionTransition> findByMissionIdOrderByDateTransitionAsc(Long missionId);
}