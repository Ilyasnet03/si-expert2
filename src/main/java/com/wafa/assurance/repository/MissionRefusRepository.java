package com.wafa.assurance.repository;

import com.wafa.assurance.model.MissionRefus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRefusRepository extends JpaRepository<MissionRefus, Long> {
    List<MissionRefus> findByMissionIdOrderByDateRefusDesc(Long missionId);
    List<MissionRefus> findAllByOrderByDateRefusDesc();
}