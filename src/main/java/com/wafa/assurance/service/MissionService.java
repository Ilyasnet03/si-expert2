package com.wafa.assurance.service;

import com.wafa.assurance.dto.MissionDTO;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.StatutMission;
import com.wafa.assurance.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    public Page<MissionDTO> search(
            String refSinistre,
            String numPolice,
            String parcours,
            StatutMission statut,
            LocalDate dateDebut,
            LocalDate dateFin,
            Pageable pageable) {
        
        List<Mission> missions = missionRepository.findAll();
        
        // Filtrer les résultats
        missions = missions.stream()
            .filter(m -> refSinistre == null || m.getRefSinistre().contains(refSinistre))
            .filter(m -> numPolice == null || m.getNumPolice().contains(numPolice))
            .filter(m -> parcours == null || m.getParcours().contains(parcours))
            .filter(m -> statut == null || m.getStatut() == statut)
            .filter(m -> {
                if (dateDebut == null && dateFin == null) return true;
                LocalDateTime createdAt = m.getDateCreation();
                if (createdAt == null) return true;
                
                if (dateDebut != null && createdAt.toLocalDate().isBefore(dateDebut)) return false;
                if (dateFin != null && createdAt.toLocalDate().isAfter(dateFin)) return false;
                return true;
            })
            .collect(Collectors.toList());

        // Convertir en DTOs et paginer
        List<MissionDTO> dtos = missions.stream()
            .map(MissionDTO::fromEntity)
            .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        
        return new PageImpl<>(
            dtos.subList(start, end),
            pageable,
            dtos.size()
        );
    }

    public MissionDTO findById(Long id) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + id));
        return MissionDTO.fromEntity(mission);
    }

    public MissionDTO accepter(Long id, boolean signalerInvestigation) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        
        mission.setStatut(StatutMission.NON_CLOTUREE);
        mission.setDateAffectation(LocalDateTime.now());
        
        if (signalerInvestigation) {
            mission.setObservations((mission.getObservations() != null ? mission.getObservations() + "\n" : "")
                + "[Investigation signalée " + LocalDateTime.now() + "]");
        }
        
        Mission updated = missionRepository.save(mission);
        return MissionDTO.fromEntity(updated);
    }

    public MissionDTO refuser(Long id, String motif) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        
        mission.setStatut(StatutMission.REFUSEE);
        mission.setMotifRefus(motif);
        
        Mission updated = missionRepository.save(mission);
        return MissionDTO.fromEntity(updated);
    }

    public MissionDTO changerStatut(Long id, StatutMission statut) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        
        mission.setStatut(statut);
        
        if (statut == StatutMission.CLOTUREE) {
            mission.setDateCloture(LocalDateTime.now());
        }
        
        Mission updated = missionRepository.save(mission);
        return MissionDTO.fromEntity(updated);
    }
}
