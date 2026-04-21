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
public class ExpertPerformanceDTO {
    private Long id;
    private String nomComplet;
    private String email;
    private String telephone;
    private String statut;
    private long missionsTraitees;
    private long missionsEnCours;
    private long missionsTerminees;
    private long missionsEnRetard;
    private double delaiMoyenJours;
    private double tauxSatisfaction;
    private double note;
    private String derniereActivite;
    private List<String> specialites;
    private List<String> zonesIntervention;
}