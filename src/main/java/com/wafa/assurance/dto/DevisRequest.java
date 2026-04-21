package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DevisRequest {
    private String garage;
    private String typeDevis;
    private BigDecimal montantPieces;
    private BigDecimal montantPeinture;
    private BigDecimal montantMainOeuvre;
    private BigDecimal montantTotal;
    private String typeOperation;
    private String typeOperationAccorde;
    private boolean expertiseContradictoire;
    private String observations;
}
