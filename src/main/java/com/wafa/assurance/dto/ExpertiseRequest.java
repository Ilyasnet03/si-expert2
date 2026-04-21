package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ExpertiseRequest {
    private LocalDate dateExpertise;
    private LocalDate dateMiseEnCirculation;
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
    private Boolean reforme;
    private String explicationReforme;
    private LocalDate dateDemandeContreExpertise;
    private String expertAdverseDesigne;
    private LocalDate dateDesignationExpertAdverse;
    private LocalDate dateExpertiseAdverse;
    private String accordExperts;
    private LocalDate dateDemandeArbitrage;
    private LocalDate dateDesignationArbitre;
    private String expertArbitreDesigne;
    private LocalDate dateExpertiseArbitrale;
    private String rapportDefinitif;
    private String observations;
}
