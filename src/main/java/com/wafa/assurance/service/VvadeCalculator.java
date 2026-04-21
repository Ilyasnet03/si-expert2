package com.wafa.assurance.service;

import com.wafa.assurance.dto.VvadeRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class VvadeCalculator {

    public BigDecimal calculer(VvadeRequest params) {
        BigDecimal valeurBase = getCoteArgus(params);
        BigDecimal coefKm = calculCoefKm(params.getKilometrage(), resolveAge(params.getDateMiseEnCirculation()));
        BigDecimal coefEtat = getCoefEtat(params.getEtatGeneral());
        BigDecimal coefEntretien = Boolean.TRUE.equals(params.getCarnetEntretienPresent()) ? BigDecimal.ONE : new BigDecimal("0.95");
        BigDecimal coefSinistre = Boolean.TRUE.equals(params.getSinistresAnterieurs()) ? new BigDecimal("0.93") : BigDecimal.ONE;

        return valeurBase
            .multiply(coefKm)
            .multiply(coefEtat)
            .multiply(coefEntretien)
            .multiply(coefSinistre)
            .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCoteArgus(VvadeRequest params) {
        if (params.getCoteArgus() != null && params.getCoteArgus().compareTo(BigDecimal.ZERO) > 0) {
            return params.getCoteArgus();
        }
        return new BigDecimal("0.00");
    }

    private long resolveAge(LocalDate dateMiseEnCirculation) {
        if (dateMiseEnCirculation == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.YEARS.between(dateMiseEnCirculation, LocalDate.now()));
    }

    private BigDecimal calculCoefKm(Integer kilometrage, long age) {
        int km = kilometrage != null ? kilometrage : 0;
        int kmReference = (int) Math.max(15000, age * 15000);
        if (km <= kmReference) {
            return BigDecimal.ONE;
        }
        if (km <= kmReference + 30000) {
            return new BigDecimal("0.95");
        }
        if (km <= kmReference + 60000) {
            return new BigDecimal("0.88");
        }
        return new BigDecimal("0.80");
    }

    private BigDecimal getCoefEtat(String etatGeneral) {
        if (etatGeneral == null) {
            return BigDecimal.ONE;
        }
        return switch (etatGeneral.toUpperCase()) {
            case "EXCELLENT" -> new BigDecimal("1.05");
            case "BON" -> BigDecimal.ONE;
            case "MOYEN" -> new BigDecimal("0.92");
            case "MAUVAIS" -> new BigDecimal("0.82");
            default -> BigDecimal.ONE;
        };
    }
}