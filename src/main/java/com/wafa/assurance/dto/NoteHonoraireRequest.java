package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class NoteHonoraireRequest {
    @NotNull
    private String numeroNote;
    
    private String description;
    
    @NotNull
    private BigDecimal montantHT;
    
    private BigDecimal tauxTVA;

    private String statut;
    
    private String observations;
}
