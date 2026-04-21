package com.wafa.assurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportConfigDTO {
    private String key;
    private String titre;
    private String description;
    private List<String> exportFormats;
    private List<String> colonnes;
    private boolean planifiable;
    private String periodeParDefaut;
}