package com.wafa.assurance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class VvadeResponse {
    private BigDecimal coteArgus;
    private BigDecimal vvade;
    private String detailsCalcul;
}