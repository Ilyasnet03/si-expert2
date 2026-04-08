package com.wafa.assurance.dto;

import com.wafa.assurance.model.Devis;
import com.wafa.assurance.model.TypeDevis;
import com.wafa.assurance.model.TypeOperation;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DevisDTO {
    private Long id;
    private Long missionId;
    private String garage;
    private TypeDevis typeDevis;
    private Double montantPieces;
    private Double montantPeinture;
    private Double montantMainOeuvre;
    private TypeOperation typeOperation;
    private Double montantTotal;
    private Boolean demandeExpertiseContradictoire;
    private String observations;
    private LocalDateTime dateCreation;
    private Double montantPiecesAccorde;
    private Double montantPeintureAccorde;
    private Double montantMainOeuvreAccorde;

    public static DevisDTO fromEntity(Devis devis) {
        DevisDTO dto = new DevisDTO();
        dto.setId(devis.getId());
        dto.setMissionId(devis.getMission().getId());
        dto.setGarage(devis.getGarage());
        dto.setTypeDevis(devis.getTypeDevis());
        dto.setMontantPieces(devis.getMontantPieces());
        dto.setMontantPeinture(devis.getMontantPeinture());
        dto.setMontantMainOeuvre(devis.getMontantMainOeuvre());
        dto.setMontantTotal(devis.getMontantTotal());
        dto.setTypeOperation(devis.getTypeOperation());
        dto.setDemandeExpertiseContradictoire(devis.getDemandeExpertiseContradictoire());
        dto.setObservations(devis.getObservations());
        dto.setDateCreation(devis.getDateCreation());
        dto.setMontantPiecesAccorde(devis.getMontantPiecesAccorde());
        dto.setMontantPeintureAccorde(devis.getMontantPeintureAccorde());
        dto.setMontantMainOeuvreAccorde(devis.getMontantMainOeuvreAccorde());
        return dto;
    }

    public Double getMontantTotalAccorde() {
        if (montantPiecesAccorde != null && montantPeintureAccorde != null && montantMainOeuvreAccorde != null) {
            return montantPiecesAccorde + montantPeintureAccorde + montantMainOeuvreAccorde;
        }
        return null;
    }
}
