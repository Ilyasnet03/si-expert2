package com.wafa.assurance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class FactureReparationRequest {
    @NotBlank
    private String numeroFacture;
    @NotNull
    private LocalDate dateFacture;
    @NotNull
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String garageEmetteur;
    private Long devisId;
    private String statutPaiement;
}