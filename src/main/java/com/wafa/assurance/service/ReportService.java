package com.wafa.assurance.service;

import com.wafa.assurance.dto.MissionAdminDTO;
import com.wafa.assurance.dto.ReportConfigDTO;
import com.wafa.assurance.dto.SinistreAdminDTO;
import com.wafa.assurance.dto.UserAdminDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MissionAdminService missionAdminService;
    private final UserManagementService userManagementService;

    public List<ReportConfigDTO> getPredefinedReports() {
        return List.of(
            new ReportConfigDTO("activite-mensuelle", "Activité mensuelle", "Missions, sinistres et montants du mois", List.of("PDF", "EXCEL", "CSV"), List.of("reference", "type", "statut", "dateCreation"), true, "30j"),
            new ReportConfigDTO("performance-experts", "Performance experts", "Classement, délais et satisfaction", List.of("PDF", "EXCEL", "CSV"), List.of("nomComplet", "missionsTraitees", "delaiMoyenJours", "tauxSatisfaction"), true, "90j"),
            new ReportConfigDTO("synthese-financiere", "Synthèse financière", "Montants de devis et d'indemnisations", List.of("PDF", "EXCEL", "CSV"), List.of("reference", "type", "priorite", "delaiJours"), true, "30j"),
            new ReportConfigDTO("delais-traitement", "Délais de traitement", "Comparatif par mission et par statut", List.of("PDF", "EXCEL", "CSV"), List.of("reference", "statut", "delaiJours"), true, "90j"),
            new ReportConfigDTO("sinistres-region", "Sinistres par région", "Répartition géographique des sinistres", List.of("PDF", "EXCEL", "CSV"), List.of("numeroSinistre", "region", "type", "statut"), true, "12m")
        );
    }

    public String exportCsv(String type) {
        return switch (type == null ? "missions" : type) {
            case "users" -> exportUsersCsv();
            case "sinistres" -> exportSinistresCsv();
            default -> exportMissionsCsv();
        };
    }

    public Map<String, Object> getSettingsSnapshot() {
        return Map.of(
            "carenceDays", 7,
            "performanceThresholds", Map.of("green", 7, "orange", 15, "red", 16),
            "notifications", Map.of("email", true, "inApp", true, "sms", false),
            "modules", Map.of("reports", true, "users", true, "audit", true)
        );
    }

    private String exportMissionsCsv() {
        List<MissionAdminDTO> missions = missionAdminService.listMissions(null, null, null);
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Reference,NumeroMission,Type,Statut,Priorite,DateCreation,DelaiJours");
        missions.forEach(mission -> joiner.add(String.join(",",
            safe(mission.getReference()),
            safe(mission.getNumeroMission()),
            safe(mission.getType()),
            safe(mission.getStatut()),
            safe(mission.getPriorite()),
            safe(String.valueOf(mission.getDateCreation())),
            safe(String.valueOf(mission.getDelaiJours()))
        )));
        return joiner.toString();
    }

    private String exportUsersCsv() {
        List<UserAdminDTO> users = userManagementService.listUsers(null, null, null);
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Id,NomComplet,Email,Role,Statut,DateCreation");
        users.forEach(user -> joiner.add(String.join(",",
            safe(String.valueOf(user.getId())),
            safe(user.getNomComplet()),
            safe(user.getEmail()),
            safe(user.getRole()),
            safe(user.getStatut()),
            safe(String.valueOf(user.getDateCreation()))
        )));
        return joiner.toString();
    }

    private String exportSinistresCsv() {
        List<SinistreAdminDTO> sinistres = missionAdminService.listSinistres(null, null, null);
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("NumeroSinistre,Type,Statut,Region,Date");
        sinistres.forEach(sinistre -> joiner.add(String.join(",",
            safe(sinistre.getNumeroSinistre()),
            safe(sinistre.getType()),
            safe(sinistre.getStatut()),
            safe(sinistre.getRegion()),
            safe(String.valueOf(sinistre.getDate()))
        )));
        return joiner.toString();
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}