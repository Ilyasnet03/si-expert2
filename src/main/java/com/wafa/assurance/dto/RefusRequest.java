package com.wafa.assurance.dto;

import com.wafa.assurance.model.MotifRefus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefusRequest {
    @NotNull
    private MotifRefus motif;
    private String commentaire;
}
