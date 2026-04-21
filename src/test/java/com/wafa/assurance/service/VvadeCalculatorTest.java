package com.wafa.assurance.service;

import com.wafa.assurance.dto.VvadeRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class VvadeCalculatorTest {

    private final VvadeCalculator calculator = new VvadeCalculator();

    @Test
    void calculer_returnsZeroWhenNoArgusValueProvided() {
        VvadeRequest request = new VvadeRequest();
        request.setKilometrage(120000);
        request.setEtatGeneral("BON");

        BigDecimal result = calculator.calculer(request);

        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    void calculer_appliesAllCoefficientsForADegradedVehicle() {
        VvadeRequest request = new VvadeRequest();
        request.setCoteArgus(new BigDecimal("100000.00"));
        request.setDateMiseEnCirculation(LocalDate.now().minusYears(4));
        request.setKilometrage(100000);
        request.setEtatGeneral("MAUVAIS");
        request.setCarnetEntretienPresent(false);
        request.setSinistresAnterieurs(true);

        BigDecimal result = calculator.calculer(request);

        assertThat(result).isEqualByComparingTo("63753.36");
    }

    @Test
    void calculer_keepsFullValueWhenVehicleIsWithinReferenceMileageAndWellMaintained() {
        VvadeRequest request = new VvadeRequest();
        request.setCoteArgus(new BigDecimal("85000.00"));
        request.setDateMiseEnCirculation(LocalDate.now().minusYears(2));
        request.setKilometrage(25000);
        request.setEtatGeneral("EXCELLENT");
        request.setCarnetEntretienPresent(true);
        request.setSinistresAnterieurs(false);

        BigDecimal result = calculator.calculer(request);

        assertThat(result).isEqualByComparingTo("89250.00");
    }

    @Test
    void getCoteArgus_returnsZeroWhenValueIsMissingOrInvalid() {
        VvadeRequest request = new VvadeRequest();
        request.setCoteArgus(BigDecimal.ZERO);

        assertThat(calculator.getCoteArgus(request)).isEqualByComparingTo("0.00");

        request.setCoteArgus(new BigDecimal("-1"));

        assertThat(calculator.getCoteArgus(request)).isEqualByComparingTo("0.00");
    }
}