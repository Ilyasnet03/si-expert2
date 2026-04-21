package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO complet pour les statistiques du dashboard admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiDashboardDTO {

    // --- Cartes KPI ---
    private long totalMissions;
    private double evolutionMissions; // % par rapport au mois dernier
    private long missionsEnCours;
    private double pourcentageMissionsEnCours; // par rapport au total
    private long expertisesCeMois;
    private double evolutionExpertises;
    private long sinistresDeclarees;
    private long montantTotalDevis; // en centimes pour éviter les problèmes de précision
    private BigDecimal montantTotalDevisBD;
    private double evolutionMontantDevis;
    private double delaiMoyenTraitement; // en jours

    // --- Compteurs par statut ---
    private long missionsNouvelles;
    private long missionsNonCloturees;
    private long missionsRefusees;
    private long missionsCarence;
    private long missionsHonoraires;
    private long missionsCloturees;

    // --- Données graphiques ---
    private List<String> moisLabels;        // ["Jan", "Fév", ...]
    private List<Long> missionsParMois;      // [10, 20, ...]
    private Map<String, Long> repartitionParType; // {"EAD": 10, "Expertise": 20, ...}
    private List<TopExpertDTO> topExperts;

    // --- Dernières missions ---
    private List<DerniereMissionDTO> dernieresMissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopExpertDTO {
        private String nom;
        private String prenom;
        private long nbMissions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DerniereMissionDTO {
        private Long id;
        private String reference;
        private String typeMission;
        private String statut;
        private String dateCreation;
        private int progression;
        private String immatriculation;
    }
}
