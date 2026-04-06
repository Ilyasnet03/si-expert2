package com.wafa.assurance.repository;

import com.wafa.assurance.model.Devis;
import com.wafa.assurance.model.StatutDevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DevisRepository extends JpaRepository<Devis, Long> {
    List<Devis> findByMissionIdOrderByCreatedAtDesc(Long missionId);
    long countByMissionIdAndStatut(Long missionId, StatutDevis statut);
}
