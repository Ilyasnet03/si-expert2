package com.wafa.assurance.dto;

import com.wafa.assurance.model.NoteHonoraire;
import com.wafa.assurance.model.StatutNoteHonoraire;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NoteHonoraireDTO {
    private Long id;
    private Long missionId;
    private String refSinistre;
    private String typeMission;
    private String numPolice;
    private String immatriculation;
    private String telAssure;
    private String parcours;
    private BigDecimal montant;
    private String numeroNote;
    private String description;
    private BigDecimal montantHT;
    private BigDecimal tauxTVA;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String observations;
    private String nomFichier;
    private String cheminFichier;
    private String urlFichier;
    private LocalDateTime dateCreation;
    private StatutNoteHonoraire statut;

    public static NoteHonoraireDTO fromEntity(NoteHonoraire noteHonoraire) {
        NoteHonoraireDTO dto = new NoteHonoraireDTO();
        dto.setId(noteHonoraire.getId());
        dto.setMissionId(noteHonoraire.getMission().getId());
        dto.setRefSinistre(noteHonoraire.getMission().getRefSinistre());
        dto.setTypeMission(noteHonoraire.getMission().getTypeMission());
        dto.setNumPolice(noteHonoraire.getMission().getNumPolice());
        dto.setImmatriculation(noteHonoraire.getMission().getImmatriculation());
        dto.setTelAssure(noteHonoraire.getMission().getTelAssure());
        dto.setParcours(noteHonoraire.getMission().getParcours());
        dto.setMontant(noteHonoraire.getMontant());
        dto.setNumeroNote(noteHonoraire.getNumeroNote());
        dto.setDescription(noteHonoraire.getDescription());
        dto.setMontantHT(noteHonoraire.getMontantHT());
        dto.setTauxTVA(noteHonoraire.getTauxTVA());
        dto.setMontantTVA(noteHonoraire.getMontantTVA());
        dto.setMontantTTC(noteHonoraire.getMontantTTC());
        dto.setObservations(noteHonoraire.getObservations());
        dto.setNomFichier(noteHonoraire.getNomFichier());
        dto.setCheminFichier(noteHonoraire.getCheminFichier());
        dto.setUrlFichier(noteHonoraire.getUrlFichier());
        dto.setDateCreation(noteHonoraire.getDateCreation());
        dto.setStatut(noteHonoraire.getStatut());
        return dto;
    }

    public static NoteHonoraireDTO from(NoteHonoraire noteHonoraire) {
        return fromEntity(noteHonoraire);
    }
}
