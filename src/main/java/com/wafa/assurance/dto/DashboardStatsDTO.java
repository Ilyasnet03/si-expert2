package com.wafa.assurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long missionsTotales;
    private double evolutionMissions;
    private long missionsEnCours;
    private double progressionMissionsEnCours;
    private long missionsCloturees;
    private double tauxCloture;
    private long expertsActifs;
    private double evolutionExperts;
    private long sinistresDeclaresMois;
    private double evolutionSinistres;
    private BigDecimal montantTotalIndemnites;
    private double delaiMoyenTraitement;
    private List<MonthlyMissionPointDTO> evolutionMensuelle;
    private List<TypeDistributionPointDTO> repartitionSinistres;
    private List<ExpertPerformanceDTO> performanceExperts;
    private DelayStatsDTO delaisTraitement;
    private List<GeoPointDTO> carteSinistres;
    private List<MissionAdminDTO> dernieresMissions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyMissionPointDTO {
        private String label;
        private long anneeCourante;
        private long anneePrecedente;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeDistributionPointDTO {
        private String type;
        private long valeur;
        private String couleur;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DelayStatsDTO {
        private double delaiMoyenGlobal;
        private String niveau;
        private List<DelayBucketDTO> histogramme;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DelayBucketDTO {
        private String label;
        private long valeur;
        private String couleur;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoPointDTO {
        private String zone;
        private String type;
        private long volume;
    }

    public static DashboardStatsDTO empty() {
        return DashboardStatsDTO.builder()
            .montantTotalIndemnites(BigDecimal.ZERO)
            .evolutionMensuelle(new ArrayList<>())
            .repartitionSinistres(new ArrayList<>())
            .performanceExperts(new ArrayList<>())
            .carteSinistres(new ArrayList<>())
            .dernieresMissions(new ArrayList<>())
            .delaisTraitement(DelayStatsDTO.builder().histogramme(new ArrayList<>()).niveau("VERT").build())
            .build();
    }
}