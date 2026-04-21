package com.wafa.assurance.dto;

import com.wafa.assurance.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionAdminDTO {
    private Long id;
    private String reference;
    private String numeroMission;
    private String assure;
    private String expert;
    private String type;
    private String parcours;
    private String numPolice;
    private String immatriculation;
    private String statut;
    private String priorite;
    private LocalDateTime dateCreation;
    private LocalDateTime dateCloture;
    private long delaiJours;
    private String observations;

    public static MissionAdminDTO from(Mission mission) {
        long delai = 0;
        if (mission.getDateCreation() != null) {
            LocalDateTime end = mission.getDateCloture() != null ? mission.getDateCloture() : LocalDateTime.now();
            delai = Math.max(0, ChronoUnit.DAYS.between(mission.getDateCreation(), end));
        }

        return MissionAdminDTO.builder()
            .id(mission.getId())
            .reference(mission.getRefSinistre())
            .numeroMission(mission.getNumeroMission())
            .assure(mission.getTelAssure())
            .expert(mission.getExpert() != null ? mission.getExpert().getPrenom() + " " + mission.getExpert().getNom() : "Non affecté")
            .type(mission.getTypeMission())
            .parcours(mission.getParcours())
            .numPolice(mission.getNumPolice())
            .immatriculation(mission.getImmatriculation())
            .statut(mission.getStatut() != null ? mission.getStatut().name() : "INCONNU")
            .priorite(resolvePriority(delai, mission.getStatut() != null ? mission.getStatut().name() : null))
            .dateCreation(mission.getDateCreation())
            .dateCloture(mission.getDateCloture())
            .delaiJours(delai)
            .observations(mission.getObservations())
            .build();
    }

    private static String resolvePriority(long delai, String statut) {
        if ("CARENCE".equals(statut) || delai > 15) {
            return "URGENTE";
        }
        if (delai > 7) {
            return "NORMALE";
        }
        return "BASSE";
    }
}