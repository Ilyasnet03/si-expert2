package com.wafa.assurance.dto;

import com.wafa.assurance.model.Expertise;
import com.wafa.assurance.model.TypeExpertise;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExpertiseDTO {
    private Long id;
    private Long missionId;
    private TypeExpertise typeExpertise;
    private LocalDateTime dateExpertise;
    private LocalDate dateMiseEnCirculation;
    private String lieu;
    private String ville;
    private String adresse;
    private Integer kilometrage;
    private String etatGeneral;
    private String etatVehicule;
    private String estimationDommages;
    private BigDecimal montantEstimation;
    private String entretien;
    private String carnetEntretien;
    private Boolean carnetEntretienPresent;
    private String optionsSpecifiques;
    private Boolean sinistresAnterieurs;
    private BigDecimal coteArgus;
    private String calculVVADE;
    private String reforme;
    private LocalDateTime dateDemandeContreExpertise;
    private String expertAdverseDesigne;
    private LocalDateTime dateDesignationExpertAdverse;
    private LocalDateTime dateExpertiseAdverse;
    private String accordExperts;
    private String arbitrage;
    private String expertiseContradictoire;
    private LocalDateTime dateExpertiseContradictoire;
    private BigDecimal montantExpertiseContradictoire;
    private LocalDateTime dateDemandeArbitrage;
    private LocalDateTime dateDesignationArbitre;
    private String expertArbitreDesigne;
    private LocalDateTime dateExpertiseArbitrale;
    private String rapportDefinitif;
    private String observations;
    private LocalDateTime dateCreation;

    public static ExpertiseDTO fromEntity(Expertise expertise) {
        ExpertiseDTO dto = new ExpertiseDTO();
        dto.setId(expertise.getId());
        dto.setMissionId(expertise.getMission().getId());
        dto.setTypeExpertise(expertise.getTypeExpertise());
        dto.setDateExpertise(expertise.getDateExpertise());
        dto.setDateMiseEnCirculation(expertise.getDateMiseEnCirculation());
        dto.setLieu(expertise.getLieu());
        dto.setVille(expertise.getVille());
        dto.setAdresse(expertise.getAdresse());
        dto.setKilometrage(expertise.getKilometrage());
        dto.setEtatGeneral(expertise.getEtatGeneral());
        dto.setEtatVehicule(expertise.getEtatVehicule());
        dto.setEstimationDommages(expertise.getEstimationDommages());
        dto.setMontantEstimation(expertise.getMontantEstimation());
        dto.setEntretien(expertise.getEntretien());
        dto.setCarnetEntretien(expertise.getCarnetEntretien());
        dto.setCarnetEntretienPresent(expertise.getCarnetEntretienPresent());
        dto.setOptionsSpecifiques(expertise.getOptionsSpecifiques());
        dto.setSinistresAnterieurs(expertise.getSinistresAnterieurs());
        dto.setCoteArgus(expertise.getCoteArgus());
        dto.setCalculVVADE(expertise.getCalculVVADE());
        dto.setReforme(expertise.getReforme());
        dto.setDateDemandeContreExpertise(expertise.getDateDemandeContreExpertise());
        dto.setExpertAdverseDesigne(expertise.getExpertAdverseDesigne());
        dto.setDateDesignationExpertAdverse(expertise.getDateDesignationExpertAdverse());
        dto.setDateExpertiseAdverse(expertise.getDateExpertiseAdverse());
        dto.setAccordExperts(expertise.getAccordExperts());
        dto.setArbitrage(expertise.getArbitrage());
        dto.setExpertiseContradictoire(expertise.getExpertiseContradictoire());
        dto.setDateExpertiseContradictoire(expertise.getDateExpertiseContradictoire());
        dto.setMontantExpertiseContradictoire(expertise.getMontantExpertiseContradictoire());
        dto.setDateDemandeArbitrage(expertise.getDateDemandeArbitrage());
        dto.setDateDesignationArbitre(expertise.getDateDesignationArbitre());
        dto.setExpertArbitreDesigne(expertise.getExpertArbitreDesigne());
        dto.setDateExpertiseArbitrale(expertise.getDateExpertiseArbitrale());
        dto.setRapportDefinitif(expertise.getRapportDefinitif());
        dto.setObservations(expertise.getObservations());
        dto.setDateCreation(expertise.getDateCreation());
        return dto;
    }

    public static ExpertiseDTO from(Expertise expertise) {
        return fromEntity(expertise);
    }
}
