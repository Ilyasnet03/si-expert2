package com.wafa.assurance.repository;

import com.wafa.assurance.model.FactureReparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureReparationRepository extends JpaRepository<FactureReparation, Long> {
    List<FactureReparation> findByMissionIdOrderByDateCreationDesc(Long missionId);
}