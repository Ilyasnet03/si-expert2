package com.wafa.assurance.dto;

import com.wafa.assurance.model.FactureReparation;
import com.wafa.assurance.model.StatutPaiementFacture;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FactureReparationDTO {
    private Long id;
    private Long missionId;
    private Long devisId;
    private String numeroFacture;
    private LocalDate dateFacture;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String garageEmetteur;
    private StatutPaiementFacture statutPaiement;
    private String nomFichier;
    private LocalDateTime dateCreation;

    public static FactureReparationDTO fromEntity(FactureReparation facture) {
        FactureReparationDTO dto = new FactureReparationDTO();
        dto.setId(facture.getId());
        dto.setMissionId(facture.getMission().getId());
        dto.setDevisId(facture.getDevis() != null ? facture.getDevis().getId() : null);
        dto.setNumeroFacture(facture.getNumeroFacture());
        dto.setDateFacture(facture.getDateFacture());
        dto.setMontantHT(facture.getMontantHT());
        dto.setMontantTVA(facture.getMontantTVA());
        dto.setMontantTTC(facture.getMontantTTC());
        dto.setGarageEmetteur(facture.getGarageEmetteur());
        dto.setStatutPaiement(facture.getStatutPaiement());
        dto.setNomFichier(facture.getNomFichier());
        dto.setDateCreation(facture.getDateCreation());
        return dto;
    }
}