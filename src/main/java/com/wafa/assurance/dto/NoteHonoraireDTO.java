package com.wafa.assurance.dto;

import com.wafa.assurance.model.NoteHonoraire;
import com.wafa.assurance.model.StatutNoteHonoraire;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NoteHonoraireDTO {
    private Long id;
    private Long missionId;
    private Double montant;
    private String urlFichier;
    private LocalDateTime dateCreation;
    private StatutNoteHonoraire statut;

    public static NoteHonoraireDTO fromEntity(NoteHonoraire noteHonoraire) {
        NoteHonoraireDTO dto = new NoteHonoraireDTO();
        dto.setId(noteHonoraire.getId());
        dto.setMissionId(noteHonoraire.getMission().getId());
        dto.setMontant(noteHonoraire.getMontant());
        dto.setUrlFichier(noteHonoraire.getUrlFichier());
        dto.setDateCreation(noteHonoraire.getDateCreation());
        dto.setStatut(noteHonoraire.getStatut());
        return dto;
    }
}
