package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ExpertiseRequest {
    private LocalDate dateExpertise;
    private String lieu;
    private Integer kilometrage;
    private String etatVehicule;
    private String estimationDommages;
    private BigDecimal montantEstimation;
    private String calculVVADE;
    private String arbitrage;
    private String expertiseContradictoire;
    private LocalDate dateExpertiseContradictoire;
    private BigDecimal montantExpertiseContradictoire;
    private String observations;
}
