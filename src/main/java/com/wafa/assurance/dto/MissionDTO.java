package com.wafa.assurance.dto;

import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.StatutMission;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MissionDTO {
    private Long id;
    private String refSinistre;
    private String numPolice;
    private String immatriculation;
    private String typeMission;
    private String parcours;
    private String telAssure;
    private StatutMission statut;
    private String motifRefus;
    private LocalDateTime createdAt;
    private LocalDateTime dateAffectation;
    private LocalDateTime dateCloture;
    private String observations;

    // Nom généré pour affichage
    public String getNumMission() {
        return "M" + String.format("%05d", this.id != null ? this.id : 0);
    }

    // Alias pour compatibilité frontend
    public String getAffecteeAt() {
        return dateAffectation != null ? dateAffectation.toString() : null;
    }

    public static MissionDTO fromEntity(Mission mission) {
        MissionDTO dto = new MissionDTO();
        dto.setId(mission.getId());
        dto.setRefSinistre(mission.getRefSinistre());
        dto.setNumPolice(mission.getNumPolice());
        dto.setImmatriculation(mission.getImmatriculation());
        dto.setTypeMission(mission.getTypeMission());
        dto.setParcours(mission.getParcours());
        dto.setTelAssure(mission.getTelAssure());
        dto.setStatut(mission.getStatut());
        dto.setMotifRefus(mission.getMotifRefus());
        dto.setCreatedAt(mission.getDateCreation());
        dto.setDateAffectation(mission.getDateAffectation());
        dto.setDateCloture(mission.getDateCloture());
        dto.setObservations(mission.getObservations());
        return dto;
    }
}
