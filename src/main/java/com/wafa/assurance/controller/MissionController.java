package com.wafa.assurance.controller;

import com.wafa.assurance.dto.MissionDTO;
import com.wafa.assurance.dto.RefusRequest;
import com.wafa.assurance.model.StatutMission;
import com.wafa.assurance.repository.MissionRepository;
import com.wafa.assurance.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "http://localhost:3000")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @Autowired
    private MissionRepository repository;

    // Constante pour le délai de carence (48h)
    private static final int DELAI_CARENCE_HEURES = 48;

    // ───────────────────────── Recherche ─────────────────────────
    @GetMapping
    public ResponseEntity<Page<MissionDTO>> searchMissions(
            @RequestParam(required = false) String refSinistre,
            @RequestParam(required = false) String numPolice,
            @RequestParam(required = false) String parcours,
            @RequestParam(required = false) StatutMission statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate dateFin,
            Pageable pageable) {
        return ResponseEntity.ok(missionService.search(refSinistre, numPolice, parcours, statut, dateDebut, dateFin, pageable));
    }

    // ───────────────────────── Détail ─────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<MissionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.findById(id));
    }

    // ───────────────────────── Acceptation / Refus ────────────────
    @PostMapping("/{id}/accepter")
    public ResponseEntity<MissionDTO> accepter(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean signalerInvestigation) {
        return ResponseEntity.ok(missionService.accepter(id, signalerInvestigation));
    }

    @PostMapping("/{id}/refuser")
    public ResponseEntity<MissionDTO> refuser(
            @PathVariable Long id,
            @RequestBody @Valid RefusRequest req) {
        return ResponseEntity.ok(missionService.refuser(id, req.getMotif()));
    }

    // ───────────────────────── Statut générique ───────────────────
    @PatchMapping("/{id}/statut")
    public ResponseEntity<MissionDTO> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutMission statut) {
        return ResponseEntity.ok(missionService.changerStatut(id, statut));
    }

    // ───────────────────────── Dashboard ────────────────────────
    @GetMapping("/dashboard/compteurs")
    public ResponseEntity<Map<String, Long>> getCompteurs() {
        Map<String, Long> compteurs = new HashMap<>();
        for (StatutMission s : StatutMission.values()) {
            compteurs.put(s.name(), repository.countByStatut(s));
        }
        return ResponseEntity.ok(compteurs);
    }

    @GetMapping("/dashboard/corbeille/{statut}")
    public ResponseEntity<List<MissionDTO>> getCorbeille(
            @PathVariable StatutMission statut,
            Pageable pageable) {
        return ResponseEntity.ok(
            repository.findByStatutOrderByDateCreationDesc(statut)
                .stream()
                .map(MissionDTO::fromEntity)
                .toList()
        );
    }

    // ───────────────────────── Compatibilité ────────────────────
    @GetMapping("/search")
    public List<MissionDTO> searchCompat(@RequestParam(required = false) String ref,
                                          @RequestParam(required = false) String statut) {
        if (statut != null) {
            try {
                StatutMission statutEnum = StatutMission.valueOf(statut);
                return repository.findByStatut(statutEnum)
                    .stream()
                    .map(MissionDTO::fromEntity)
                    .toList();
            } catch (IllegalArgumentException e) {
                return handleSpecialStatut(statut);
            }
        }
        if (ref != null) {
            return repository.findByRefSinistreContainingIgnoreCase(ref)
                .stream()
                .map(MissionDTO::fromEntity)
                .toList();
        }
        return repository.findAll()
            .stream()
            .map(MissionDTO::fromEntity)
            .toList();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countByStatut(@RequestParam String statut) {
        try {
            StatutMission statutEnum = StatutMission.valueOf(statut);
            return ResponseEntity.ok(repository.countByStatut(statutEnum));
        } catch (IllegalArgumentException e) {
            return handleSpecialCount(statut);
        }
    }

    @GetMapping("/dashboard/counts")
    public ResponseEntity<Map<String, Long>> getAllCounts() {
        List<Object[]> results = repository.countMissionsByStatut();
        Map<String, Long> counts = new HashMap<>();

        for (Object[] result : results) {
            StatutMission s = (StatutMission) result[0];
            Long count = (Long) result[1];
            counts.put(s.name(), count);
        }

        // Missions en carence
        long carenceCount = repository.findMissionsEnCarence(
                LocalDateTime.now().minusHours(DELAI_CARENCE_HEURES)
        ).size();
        counts.put("CARENCE", carenceCount);

        return ResponseEntity.ok(counts);
    }

    private List<MissionDTO> handleSpecialStatut(String statut) {
        return switch (statut) {
            case "NON_CLOTUREE", "MISSION NON CLOTUREE" -> repository.findMissionsNonCloturees()
                .stream()
                .map(MissionDTO::fromEntity)
                .toList();
            case "CARENCE" -> repository.findMissionsEnCarence(
                    LocalDateTime.now().minusHours(DELAI_CARENCE_HEURES)
                ).stream()
                .map(MissionDTO::fromEntity)
                .toList();
            default -> repository.findAll()
                .stream()
                .map(MissionDTO::fromEntity)
                .toList();
        };
    }

    private ResponseEntity<Long> handleSpecialCount(String statut) {
        long count = switch (statut) {
            case "NON_CLOTUREE", "MISSION NON CLOTUREE" -> repository.findMissionsNonCloturees().size();
            case "CARENCE" -> repository.findMissionsEnCarence(
                    LocalDateTime.now().minusHours(DELAI_CARENCE_HEURES)
            ).size();
            default -> 0L;
        };
        return ResponseEntity.ok(count);
    }
}