package com.wafa.assurance.service;

import com.wafa.assurance.dto.MissionProgressDTO;
import com.wafa.assurance.dto.MissionDetailAdminDTO;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@Service
public class AdminService {
    @PreAuthorize("hasRole('ADMIN')")
    public List<MissionProgressDTO> getAllMissionsProgress() {
        // TODO: Implémenter la récupération et le calcul d'avancement
        return List.of();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MissionDetailAdminDTO getMissionDetail(Long id) {
        // TODO: Implémenter la récupération détaillée
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<?> getAllSinistres() {
        // TODO: Implémenter la récupération des sinistres
        return List.of();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Object getStats() {
        // TODO: Implémenter les statistiques globales
        return null;
    }
}
