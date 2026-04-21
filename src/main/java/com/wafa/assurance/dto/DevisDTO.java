package com.wafa.assurance.dto;

import com.wafa.assurance.model.Devis;
import com.wafa.assurance.model.StatutDevis;
import com.wafa.assurance.model.TypeDevis;
import com.wafa.assurance.model.TypeOperation;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DevisDTO {
    private Long id;
    private Long missionId;
    private String garage;
    private TypeDevis typeDevis;
    private BigDecimal montantPieces;
    private BigDecimal montantPeinture;
    private BigDecimal montantMainOeuvre;
    private TypeOperation typeOperation;
    private BigDecimal montantTotal;
    private Boolean demandeExpertiseContradictoire;
    private String observations;
    private StatutDevis statut;
    private String cheminImage;
    private LocalDateTime dateCreation;
    private BigDecimal montantPiecesAccorde;
    private BigDecimal montantPeintureAccorde;
    private BigDecimal montantMainOeuvreAccorde;
    private TypeOperation typeOperationAccorde;

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
        dto.setStatut(devis.getStatut());
        dto.setCheminImage(devis.getCheminImage());
        dto.setDateCreation(devis.getDateCreation());
        dto.setMontantPiecesAccorde(devis.getMontantPiecesAccorde());
        dto.setMontantPeintureAccorde(devis.getMontantPeintureAccorde());
        dto.setMontantMainOeuvreAccorde(devis.getMontantMainOeuvreAccorde());
        dto.setTypeOperationAccorde(devis.getTypeOperationAccorde());
        return dto;
    }

    public BigDecimal getMontantTotalAccorde() {
        if (montantPiecesAccorde != null && montantPeintureAccorde != null && montantMainOeuvreAccorde != null) {
            return montantPiecesAccorde.add(montantPeintureAccorde).add(montantMainOeuvreAccorde);
        }
        return null;
    }
}
