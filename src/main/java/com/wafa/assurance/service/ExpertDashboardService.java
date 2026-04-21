package com.wafa.assurance.service;

import com.wafa.assurance.dto.KpiDashboardDTO;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class ExpertDashboardService {
    public KpiDashboardDTO getKpis() {
        // TODO: Remplacer par des requêtes réelles pour l'expert connecté
        KpiDashboardDTO dto = new KpiDashboardDTO();
        dto.setTotalMissions(12);
        dto.setExpertisesCeMois(8);
        dto.setSinistresDeclarees(6);
        dto.setMontantTotalDevisBD(new BigDecimal("15000.00"));
        return dto;
    }
}
