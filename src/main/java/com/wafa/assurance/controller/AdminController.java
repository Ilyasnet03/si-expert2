package com.wafa.assurance.controller;

import com.wafa.assurance.dto.DashboardStatsDTO;
import com.wafa.assurance.dto.ExpertPerformanceDTO;
import com.wafa.assurance.dto.MissionAdminDTO;
import com.wafa.assurance.dto.MissionProgressDTO;
import com.wafa.assurance.dto.MissionDetailAdminDTO;
import com.wafa.assurance.dto.MissionDTO;
import com.wafa.assurance.dto.MissionRefusDTO;
import com.wafa.assurance.dto.MissionReouvertureRequest;
import com.wafa.assurance.dto.ReaffectationRequest;
import com.wafa.assurance.dto.ReportConfigDTO;
import com.wafa.assurance.dto.SinistreAdminDTO;
import com.wafa.assurance.dto.UserAdminDTO;
import com.wafa.assurance.service.AdminDashboardService;
import com.wafa.assurance.service.MissionService;
import com.wafa.assurance.service.MissionAdminService;
import com.wafa.assurance.service.ReportService;
import com.wafa.assurance.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminDashboardService adminDashboardService;
    private final MissionAdminService missionAdminService;
    private final MissionService missionService;
    private final UserManagementService userManagementService;
    private final ReportService reportService;

    @GetMapping("/dashboard/overview")
    public DashboardStatsDTO getDashboardOverview() {
        return adminDashboardService.getDashboardStats();
    }

    @GetMapping("/dashboard/recent-missions")
    public List<MissionAdminDTO> getRecentMissions(@RequestParam(defaultValue = "10") int limit) {
        return adminDashboardService.getDashboardStats().getDernieresMissions().stream().limit(limit).toList();
    }

    @GetMapping("/experts/supervision")
    public List<ExpertPerformanceDTO> getExpertsToSupervise() {
        return adminDashboardService.getDashboardStats().getPerformanceExperts();
    }

    @GetMapping("/missions")
    public List<MissionAdminDTO> getAllMissions(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String statut,
        @RequestParam(required = false) String type
    ) {
        return missionAdminService.listMissions(query, statut, type);
    }

    @GetMapping("/missions/{id}")
    public MissionDetailAdminDTO getMissionDetail(@PathVariable Long id) {
        return missionAdminService.getMissionDetail(id);
    }

    @GetMapping("/missions/refusees")
    public List<MissionRefusDTO> getMissionsRefusees() {
        return missionService.listRefuseesPourAdmin();
    }

    @PostMapping("/missions/{id}/reaffecter")
    public MissionDTO reaffecterMission(@PathVariable Long id, @RequestBody @Valid ReaffectationRequest request) {
        return missionService.reaffecter(id, request.getExpertId());
    }

    @PostMapping("/missions/{id}/rouvrir")
    public MissionDTO rouvrirMission(@PathVariable Long id, @RequestBody @Valid MissionReouvertureRequest request) {
        return missionService.rouvrir(id, request);
    }

    @GetMapping("/sinistres")
    public List<SinistreAdminDTO> getAllSinistres(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String statut
    ) {
        return missionAdminService.listSinistres(query, type, statut);
    }

    @GetMapping("/users")
    public List<UserAdminDTO> listUsers(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String statut
    ) {
        return userManagementService.listUsers(query, role, statut);
    }

    @PostMapping("/users")
    public UserAdminDTO createUser(@RequestBody UserAdminDTO payload) {
        return userManagementService.create(payload);
    }

    @PutMapping("/users/{id}")
    public UserAdminDTO updateUser(@PathVariable Long id, @RequestBody UserAdminDTO payload) {
        return userManagementService.update(id, payload);
    }

    @PostMapping("/users/{id}/toggle-active")
    public UserAdminDTO toggleUserActive(@PathVariable Long id) {
        return userManagementService.toggleActive(id);
    }

    @PostMapping("/users/{id}/reset-password")
    public UserAdminDTO resetUserPassword(@PathVariable Long id) {
        return userManagementService.resetPassword(id);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestParam(required = false) String reason) {
        userManagementService.softDelete(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports/configs")
    public List<ReportConfigDTO> getReportConfigs() {
        return reportService.getPredefinedReports();
    }

    @GetMapping("/reports/export")
    public ResponseEntity<byte[]> exportReport(@RequestParam(defaultValue = "missions") String type) {
        byte[] bytes = reportService.exportCsv(type).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + ".csv")
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(bytes);
    }

    @GetMapping("/settings")
    public Map<String, Object> getSettings() {
        return reportService.getSettingsSnapshot();
    }

    @GetMapping("/stats")
    public List<MissionProgressDTO> getStats() {
        return List.of();
    }
}
