package com.wafa.assurance.repository;

import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.StatutMission;
import com.wafa.assurance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findByStatut(StatutMission statut);

    // ✅ Compter par statut - POUR LES CORBEILLES
    long countByStatut(StatutMission statut);

    // ✅ Recherche par référence sinistre (pour autocomplétion)
    List<Mission> findByRefSinistreContainingIgnoreCase(String refSinistre);

    // ✅ Missions récentes (dernières 24h)
    List<Mission> findByDateCreationAfter(LocalDateTime date);

    // ✅ Missions par période
    List<Mission> findByDateCreationBetween(LocalDateTime debut, LocalDateTime fin);

    // ✅ Missions en carence (délai dépassé)
    @Query("SELECT m FROM Mission m WHERE m.dateAffectation IS NOT NULL " +
            "AND m.dateAffectation < :dateLimite " +
            "AND m.statut != com.wafa.assurance.model.StatutMission.CLOTUREE")
    List<Mission> findMissionsEnCarence(@Param("dateLimite") LocalDateTime dateLimite);

    @Query("SELECT m FROM Mission m WHERE m.expert = :expert " +
        "AND m.estEnCarence = true " +
        "ORDER BY m.dateCarence DESC, m.dateCreation DESC")
    List<Mission> findByExpertAndEstEnCarenceTrueOrderByDateCarenceDesc(@Param("expert") User expert);

    List<Mission> findByExpertAndStatutOrderByDateCreationDesc(User expert, StatutMission statut);

    List<Mission> findByStatutIn(List<StatutMission> statuts);

    @Query("SELECT m FROM Mission m WHERE m.statut IN :statuts AND m.estEnCarence = false AND m.dateAffectation IS NOT NULL")
    List<Mission> findEligibleForCarence(@Param("statuts") List<StatutMission> statuts);

    @Query("SELECT m FROM Mission m WHERE m.statut = com.wafa.assurance.model.StatutMission.REFUSEE ORDER BY m.dateCreation DESC")
    List<Mission> findRefuseesOrderByDateCreationDesc();

    // ✅ Missions non clôturées (spécifique)
    @Query("SELECT m FROM Mission m WHERE m.statut != com.wafa.assurance.model.StatutMission.CLOTUREE")
    List<Mission> findMissionsNonCloturees();

    // ✅ Récupérer tous les statuts avec leurs compteurs (pour dashboard)
    @Query("SELECT m.statut, COUNT(m) FROM Mission m GROUP BY m.statut")
    List<Object[]> countMissionsByStatut();

    // ✅ Recherche avancée pour le tableau (avec pagination possible)
    List<Mission> findByStatutOrderByDateCreationDesc(StatutMission statut);

    // ✅ Missions par téléphone assuré
    List<Mission> findByTelAssure(String telAssure);

    // ✅ Missions sans date d'affectation (nouvelles)
    List<Mission> findByDateAffectationIsNull();
}