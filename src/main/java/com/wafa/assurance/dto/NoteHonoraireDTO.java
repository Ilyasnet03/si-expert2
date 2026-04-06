package com.wafa.assurance.dto;

import com.wafa.assurance.model.NoteHonoraire;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NoteHonoraireDTO {
    private Long id;
    private Long missionId;
    private String numeroNote;
    private String description;
    private BigDecimal montantHT;
    private BigDecimal tauxTVA;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String cheminFichier;
    private String nomFichier;
    private String statutPaiement;
    private String observations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoteHonoraireDTO from(NoteHonoraire noteHonoraire) {
        NoteHonoraireDTO dto = new NoteHonoraireDTO();
        dto.setId(noteHonoraire.getId());
        dto.setMissionId(noteHonoraire.getMission().getId());
        dto.setNumeroNote(noteHonoraire.getNumeroNote());
        dto.setDescription(noteHonoraire.getDescription());
        dto.setMontantHT(noteHonoraire.getMontantHT());
        dto.setTauxTVA(noteHonoraire.getTauxTVA());
        dto.setMontantTVA(noteHonoraire.getMontantTVA());
        dto.setMontantTTC(noteHonoraire.getMontantTTC());
        dto.setCheminFichier(noteHonoraire.getCheminFichier());
        dto.setNomFichier(noteHonoraire.getNomFichier());
        dto.setStatutPaiement(noteHonoraire.getStatutPaiement());
        dto.setObservations(noteHonoraire.getObservations());
        dto.setCreatedAt(noteHonoraire.getCreatedAt());
        dto.setUpdatedAt(noteHonoraire.getUpdatedAt());
        return dto;
    }
}
