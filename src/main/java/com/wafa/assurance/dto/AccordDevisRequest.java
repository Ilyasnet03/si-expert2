package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AccordDevisRequest {
    private BigDecimal montantAccordePieces;
    private BigDecimal montantAccordePeinture;
    private BigDecimal montantAccordeMainOeuvre;
    private BigDecimal montantAccordeTotal;
    private String typeOperationAccorde;
    private String observations;
}
