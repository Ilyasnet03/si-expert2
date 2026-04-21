package com.wafa.assurance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VvadeRequest {
    private String marque;
    private String modele;
    private LocalDate dateMiseEnCirculation;
    private Integer kilometrage;
    private String etatGeneral;
    private Boolean carnetEntretienPresent;
    private String optionsSpecifiques;
    private Boolean sinistresAnterieurs;
    private BigDecimal coteArgus;
}