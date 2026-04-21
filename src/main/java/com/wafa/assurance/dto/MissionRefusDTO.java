package com.wafa.assurance.dto;

import com.wafa.assurance.model.MissionRefus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MissionRefusDTO {
    private Long id;
    private Long missionId;
    private Long expertId;
    private String expertNom;
    private String motif;
    private String motifLibelle;
    private String commentaire;
    private LocalDateTime dateRefus;

    public static MissionRefusDTO fromEntity(MissionRefus refus) {
        MissionRefusDTO dto = new MissionRefusDTO();
        dto.setId(refus.getId());
        dto.setMissionId(refus.getMission().getId());
        dto.setExpertId(refus.getExpert().getId());
        dto.setExpertNom(refus.getExpert().getPrenom() + " " + refus.getExpert().getNom());
        dto.setMotif(refus.getMotif().name());
        dto.setMotifLibelle(refus.getMotif().getLibelle());
        dto.setCommentaire(refus.getCommentaire());
        dto.setDateRefus(refus.getDateRefus());
        return dto;
    }
}