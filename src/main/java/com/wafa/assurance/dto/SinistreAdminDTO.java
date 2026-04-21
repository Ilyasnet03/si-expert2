package com.wafa.assurance.dto;

import com.wafa.assurance.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SinistreAdminDTO {
    private Long id;
    private String numeroSinistre;
    private LocalDateTime date;
    private String type;
    private String assure;
    private String missionLiee;
    private BigDecimal montantEstime;
    private String statut;
    private String region;
    private String ville;

    public static SinistreAdminDTO from(Mission mission) {
        return SinistreAdminDTO.builder()
            .id(mission.getId())
            .numeroSinistre(mission.getRefSinistre())
            .date(mission.getDateCreation())
            .type(mission.getTypeMission())
            .assure(mission.getTelAssure())
            .missionLiee(mission.getNumeroMission())
            .montantEstime(BigDecimal.ZERO)
            .statut(mission.getStatut() != null ? mission.getStatut().name() : "INCONNU")
            .region(mission.getParcours())
            .ville(mission.getParcours())
            .build();
    }
}