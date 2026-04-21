package com.wafa.assurance.service;

import com.wafa.assurance.dto.DevisDTO;
import com.wafa.assurance.dto.ExpertiseDTO;
import com.wafa.assurance.dto.MissionAdminDTO;
import com.wafa.assurance.dto.MissionDetailAdminDTO;
import com.wafa.assurance.dto.NoteHonoraireDTO;
import com.wafa.assurance.dto.PhotoDTO;
import com.wafa.assurance.dto.SinistreAdminDTO;
import com.wafa.assurance.dto.SinistreDTO;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.repository.DevisRepository;
import com.wafa.assurance.repository.ExpertiseRepository;
import com.wafa.assurance.repository.MissionRepository;
import com.wafa.assurance.repository.NoteHonoraireRepository;
import com.wafa.assurance.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionAdminService {

    private final MissionRepository missionRepository;
    private final ExpertiseRepository expertiseRepository;
    private final DevisRepository devisRepository;
    private final PhotoRepository photoRepository;
    private final NoteHonoraireRepository noteHonoraireRepository;

    public List<MissionAdminDTO> listMissions(String query, String statut, String type) {
        return missionRepository.findAll().stream()
            .filter(mission -> matchesMission(mission, query, statut, type))
            .sorted(Comparator.comparing(Mission::getDateCreation, Comparator.nullsLast(Comparator.reverseOrder())))
            .map(MissionAdminDTO::from)
            .collect(Collectors.toList());
    }

    public MissionDetailAdminDTO getMissionDetail(Long id) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mission introuvable: " + id));

        MissionDetailAdminDTO dto = new MissionDetailAdminDTO();
        dto.setId(mission.getId());
        dto.setNumeroMission(mission.getNumeroMission());
        dto.setTypeSinistre(mission.getTypeMission());
        dto.setStatut(mission.getStatut() != null ? mission.getStatut().name() : "INCONNU");
        dto.setDateCreation(mission.getDateCreation());
        dto.setDerniereActivite(mission.getDateCloture() != null ? mission.getDateCloture() : mission.getDateAffectation());
        dto.setSinistre(SinistreDTO.from(mission));
        dto.setExpertises(expertiseRepository.findByMissionIdOrderByDateExpertiseDesc(id).stream().map(ExpertiseDTO::from).toList());
        dto.setDevis(devisRepository.findByMissionIdOrderByDateCreationDesc(id).stream().map(DevisDTO::fromEntity).toList());
        dto.setPhotos(photoRepository.findByMissionIdOrderByDateUploadDesc(id).stream().map(PhotoDTO::from).toList());
        dto.setNotesHonoraire(noteHonoraireRepository.findByMissionIdOrderByDateCreationDesc(id).stream().map(NoteHonoraireDTO::from).toList());
        dto.setEtapes(buildEtapes(mission));
        dto.setProgression(resolveProgression(mission));
        return dto;
    }

    public List<SinistreAdminDTO> listSinistres(String query, String type, String statut) {
        return missionRepository.findAll().stream()
            .filter(mission -> matchesSinistre(mission, query, type, statut))
            .sorted(Comparator.comparing(Mission::getDateCreation, Comparator.nullsLast(Comparator.reverseOrder())))
            .map(SinistreAdminDTO::from)
            .toList();
    }

    private boolean matchesMission(Mission mission, String query, String statut, String type) {
        String normalizedQuery = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        boolean matchesQuery = normalizedQuery.isBlank()
            || contains(mission.getRefSinistre(), normalizedQuery)
            || contains(mission.getNumeroMission(), normalizedQuery)
            || contains(mission.getTelAssure(), normalizedQuery)
            || contains(mission.getImmatriculation(), normalizedQuery);

        boolean matchesStatut = statut == null || statut.isBlank()
            || (mission.getStatut() != null && statut.equalsIgnoreCase(mission.getStatut().name()));

        boolean matchesType = type == null || type.isBlank() || contains(mission.getTypeMission(), type.toLowerCase(Locale.ROOT));
        return matchesQuery && matchesStatut && matchesType;
    }

    private boolean matchesSinistre(Mission mission, String query, String type, String statut) {
        return matchesMission(mission, query, statut, type);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private List<String> buildEtapes(Mission mission) {
        List<String> etapes = new ArrayList<>();
        etapes.add("Création mission");
        if (mission.getDateAffectation() != null) {
            etapes.add("Affectation");
        }
        if (!devisRepository.findByMissionIdOrderByDateCreationDesc(mission.getId()).isEmpty()) {
            etapes.add("Devis déposés");
        }
        if (!noteHonoraireRepository.findByMissionIdOrderByDateCreationDesc(mission.getId()).isEmpty()) {
            etapes.add("Honoraires émis");
        }
        if (mission.getDateCloture() != null) {
            etapes.add("Clôture");
        }
        return etapes;
    }

    private int resolveProgression(Mission mission) {
        if (mission.getStatut() == null) {
            return 0;
        }
        return switch (mission.getStatut()) {
            case NOUVELLE -> 10;
            case ACCEPTEE -> 35;
            case NON_CLOTUREE -> 50;
            case EN_COURS -> 60;
            case CARENCE -> 40;
            case HONORAIRES -> 75;
            case REEXAMEN -> 65;
            case REFUSEE, CLOTUREE -> 100;
        };
    }
}