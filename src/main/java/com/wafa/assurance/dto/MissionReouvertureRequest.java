package com.wafa.assurance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MissionReouvertureRequest {
    @NotBlank
    private String motif;
    private String commentaire;
}