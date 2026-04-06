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
    private BigDecimal montantTotal;
    private BigDecimal montantAccordePieces;
    private BigDecimal montantAccordePeinture;
    private BigDecimal montantAccordeMainOeuvre;
    private BigDecimal montantAccordeTotal;
    private TypeOperation typeOperation;
    private boolean expertiseContradictoire;
    private String observations;
    private StatutDevis statut;
    private String cheminImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
        dto.setMontantAccordePieces(devis.getMontantAccordePieces());
        dto.setMontantAccordePeinture(devis.getMontantAccordePeinture());
        dto.setMontantAccordeMainOeuvre(devis.getMontantAccordeMainOeuvre());
        dto.setMontantAccordeTotal(devis.getMontantAccordeTotal());
        dto.setTypeOperation(devis.getTypeOperation());
        dto.setExpertiseContradictoire(devis.isExpertiseContradictoire());
        dto.setObservations(devis.getObservations());
        dto.setStatut(devis.getStatut());
        dto.setCheminImage(devis.getCheminImage());
        dto.setCreatedAt(devis.getCreatedAt());
        dto.setUpdatedAt(devis.getUpdatedAt());
        return dto;
    }
}
