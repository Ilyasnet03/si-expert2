package com.wafa.assurance.controller;

import com.wafa.assurance.dto.ActiviteRecenteDTO;
import com.wafa.assurance.dto.KpiDashboardDTO;
import com.wafa.assurance.dto.NotificationDTO;
import com.wafa.assurance.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Contrôleur du dashboard administrateur.
 * - GET /admin/dashboard : page Thymeleaf
 * - GET /api/admin/dashboard/* : endpoints REST pour les graphiques (chargement AJAX)
 */
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EXPERT')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    /**
     * Affiche la page principale du dashboard admin (Thymeleaf).
     */
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        KpiDashboardDTO kpis = dashboardService.getKpis();
        List<NotificationDTO> notifications = dashboardService.getNotifications();

        model.addAttribute("kpis", kpis);
        model.addAttribute("notifications", notifications);
        model.addAttribute("nbNotifications", notifications.stream().filter(n -> !n.isLu()).count());
        model.addAttribute("userName", authentication != null ? authentication.getName() : "Admin");

        return "admin/dashboard";
    }

    /**
     * Endpoint REST : statistiques complètes (pour les graphiques Chart.js).
     */
    @GetMapping("/api/admin/dashboard/stats")
    @ResponseBody
    public ResponseEntity<KpiDashboardDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getKpis());
    }

    /**
     * Endpoint REST : activités récentes.
     */
    @GetMapping("/api/admin/dashboard/activities")
    @ResponseBody
    public ResponseEntity<List<ActiviteRecenteDTO>> getActivities() {
        return ResponseEntity.ok(dashboardService.getActivitesRecentes());
    }

    /**
     * Endpoint REST : notifications.
     */
    @GetMapping("/api/admin/dashboard/notifications")
    @ResponseBody
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        return ResponseEntity.ok(dashboardService.getNotifications());
    }
}
