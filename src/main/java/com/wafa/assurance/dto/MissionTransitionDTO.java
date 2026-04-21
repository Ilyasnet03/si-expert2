package com.wafa.assurance.dto;

import com.wafa.assurance.model.MissionTransition;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MissionTransitionDTO {
    private Long id;
    private String ancienStatut;
    private String nouveauStatut;
    private Long acteurId;
    private String acteurNom;
    private String acteurRole;
    private String commentaire;
    private LocalDateTime dateTransition;

    public static MissionTransitionDTO fromEntity(MissionTransition transition) {
        MissionTransitionDTO dto = new MissionTransitionDTO();
        dto.setId(transition.getId());
        dto.setAncienStatut(transition.getAncienStatut() != null ? transition.getAncienStatut().name() : null);
        dto.setNouveauStatut(transition.getNouveauStatut().name());
        dto.setActeurId(transition.getActeurId());
        dto.setActeurNom(transition.getActeurNom());
        dto.setActeurRole(transition.getActeurRole());
        dto.setCommentaire(transition.getCommentaire());
        dto.setDateTransition(transition.getDateTransition());
        return dto;
    }
}