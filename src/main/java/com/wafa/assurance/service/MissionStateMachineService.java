package com.wafa.assurance.service;

import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.MissionTransition;
import com.wafa.assurance.model.StatutMission;
import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.MissionRepository;
import com.wafa.assurance.repository.MissionTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MissionStateMachineService {

    private final MissionRepository missionRepository;
    private final MissionTransitionRepository missionTransitionRepository;

    private static final Map<StatutMission, List<StatutMission>> TRANSITIONS = new EnumMap<>(StatutMission.class);

    static {
        TRANSITIONS.put(StatutMission.NOUVELLE, List.of(StatutMission.ACCEPTEE, StatutMission.NON_CLOTUREE, StatutMission.REFUSEE));
        TRANSITIONS.put(StatutMission.ACCEPTEE, List.of(StatutMission.EN_COURS, StatutMission.CARENCE, StatutMission.CLOTUREE));
        TRANSITIONS.put(StatutMission.NON_CLOTUREE, List.of(StatutMission.EN_COURS, StatutMission.CARENCE, StatutMission.CLOTUREE, StatutMission.HONORAIRES));
        TRANSITIONS.put(StatutMission.EN_COURS, List.of(StatutMission.CLOTUREE, StatutMission.HONORAIRES));
        TRANSITIONS.put(StatutMission.CARENCE, List.of(StatutMission.EN_COURS, StatutMission.NON_CLOTUREE, StatutMission.CLOTUREE, StatutMission.HONORAIRES));
        TRANSITIONS.put(StatutMission.REFUSEE, List.of(StatutMission.NOUVELLE));
        TRANSITIONS.put(StatutMission.CLOTUREE, List.of(StatutMission.REEXAMEN));
        TRANSITIONS.put(StatutMission.REEXAMEN, List.of(StatutMission.EN_COURS, StatutMission.CLOTUREE));
        TRANSITIONS.put(StatutMission.HONORAIRES, List.of(StatutMission.CLOTUREE));
    }

    public Mission transition(Mission mission, StatutMission nouveauStatut, User acteur, String commentaire) {
        StatutMission ancienStatut = mission.getStatut();
        if (ancienStatut != null && !ancienStatut.equals(nouveauStatut)) {
            List<StatutMission> autorisees = TRANSITIONS.getOrDefault(ancienStatut, List.of());
            if (!autorisees.contains(nouveauStatut)) {
                throw new IllegalStateException("Transition interdite: " + ancienStatut + " -> " + nouveauStatut);
            }
        }

        mission.setStatut(nouveauStatut);
        Mission saved = missionRepository.save(mission);

        MissionTransition transition = new MissionTransition();
        transition.setMission(saved);
        transition.setAncienStatut(ancienStatut);
        transition.setNouveauStatut(nouveauStatut);
        transition.setActeurId(acteur != null ? acteur.getId() : null);
        transition.setActeurNom(acteur != null ? acteur.getPrenom() + " " + acteur.getNom() : "Système");
        transition.setActeurRole(acteur != null ? acteur.getRole() : "SYSTEM");
        transition.setCommentaire(commentaire);
        missionTransitionRepository.save(transition);
        return saved;
    }
}