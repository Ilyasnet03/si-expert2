package com.wafa.assurance.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('EXPERT')")
public class ExpertDashboardController {
    @GetMapping("/expert/dashboard")
    public String dashboard() {
        return "redirect:/admin/dashboard";
    }
}
