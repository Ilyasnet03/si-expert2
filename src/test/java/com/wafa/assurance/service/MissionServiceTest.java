package com.wafa.assurance.service;

import com.wafa.assurance.dto.RefusRequest;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.MotifRefus;
import com.wafa.assurance.model.MissionRefus;
import com.wafa.assurance.model.StatutMission;
import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.DevisRepository;
import com.wafa.assurance.repository.ExpertiseRepository;
import com.wafa.assurance.repository.MissionRefusRepository;
import com.wafa.assurance.repository.MissionRepository;
import com.wafa.assurance.repository.MissionReouvertureRepository;
import com.wafa.assurance.repository.MissionTransitionRepository;
import com.wafa.assurance.repository.NoteHonoraireRepository;
import com.wafa.assurance.repository.PhotoRepository;
import com.wafa.assurance.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private DevisRepository devisRepository;

    @Mock
    private ExpertiseRepository expertiseRepository;

    @Mock
    private NoteHonoraireRepository noteHonoraireRepository;

    @Mock
    private NotificationCenterService notificationCenterService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private MissionStateMachineService missionStateMachineService;

    @Mock
    private MissionRefusRepository missionRefusRepository;

    @Mock
    private MissionTransitionRepository missionTransitionRepository;

    @Mock
    private MissionReouvertureRepository missionReouvertureRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MissionService missionService;

    @Test
    void accepter_assignsCurrentExpertAndPublishesNotification() {
        Mission mission = createMission(10L, "SIN-10", StatutMission.NOUVELLE);
        User expert = createUser(5L, "expert@wafa.ma", "Expert", "One");

        when(missionRepository.findById(10L)).thenReturn(Optional.of(mission));
        when(currentUserService.requireCurrentUser()).thenReturn(expert);
        when(missionStateMachineService.transition(eq(mission), eq(StatutMission.NON_CLOTUREE), eq(expert), contains("Mission acceptée")))
            .thenAnswer(invocation -> {
                mission.setStatut(StatutMission.NON_CLOTUREE);
                return mission;
            });

        var result = missionService.accepter(10L, true);

        assertThat(result.getStatut()).isEqualTo(StatutMission.NON_CLOTUREE);
        assertThat(result.getExpertId()).isEqualTo(5L);
        assertThat(mission.getExpert()).isEqualTo(expert);
        assertThat(mission.getDateAffectation()).isNotNull();
        assertThat(mission.getEstEnCarence()).isFalse();
        assertThat(mission.getObservations()).contains("Investigation signalée");
        verify(notificationCenterService).publish(
            eq("MISSION_ASSIGNEE"),
            eq("Nouvelle mission assignée"),
            contains("SIN-10"),
            eq("/missions/10")
        );
    }

    @Test
    void refuser_persistsReasonAndTransition() {
        Mission mission = createMission(11L, "SIN-11", StatutMission.NOUVELLE);
        User expert = createUser(9L, "expert2@wafa.ma", "Expert", "Two");
        RefusRequest request = new RefusRequest();
        request.setMotif(MotifRefus.AUTRE);
        request.setCommentaire("Dossier incomplet");

        when(missionRepository.findById(11L)).thenReturn(Optional.of(mission));
        when(currentUserService.requireCurrentUser()).thenReturn(expert);
        when(missionStateMachineService.transition(eq(mission), eq(StatutMission.REFUSEE), eq(expert), contains("Dossier incomplet")))
            .thenAnswer(invocation -> {
                mission.setStatut(StatutMission.REFUSEE);
                return mission;
            });

        var result = missionService.refuser(11L, request);

        assertThat(result.getStatut()).isEqualTo(StatutMission.REFUSEE);
        assertThat(mission.getMotifRefus()).contains("Autre").contains("Dossier incomplet");

        ArgumentCaptor<MissionRefus> refusalCaptor = ArgumentCaptor.forClass(MissionRefus.class);
        verify(missionRefusRepository).save(refusalCaptor.capture());
        MissionRefus savedRefusal = refusalCaptor.getValue();
        assertThat(savedRefusal.getMission()).isEqualTo(mission);
        assertThat(savedRefusal.getExpert()).isEqualTo(expert);
        assertThat(savedRefusal.getMotif()).isEqualTo(MotifRefus.AUTRE);
        assertThat(savedRefusal.getCommentaire()).isEqualTo("Dossier incomplet");

        verify(notificationCenterService).publish(
            eq("MISSION_REFUSEE"),
            eq("Mission refusée"),
            contains("SIN-11"),
            eq("/missions/11")
        );
    }

    @Test
    void cloturer_setsCloseDateAndClearsCarence() {
        Mission mission = createMission(12L, "SIN-12", StatutMission.EN_COURS);
        mission.setEstEnCarence(true);
        User actor = createUser(4L, "admin@wafa.ma", "Admin", "User");

        when(missionRepository.findById(12L)).thenReturn(Optional.of(mission));
        when(currentUserService.requireCurrentUser()).thenReturn(actor);
        when(missionStateMachineService.transition(eq(mission), eq(StatutMission.CLOTUREE), eq(actor), eq("Mission clôturée.")))
            .thenAnswer(invocation -> {
                mission.setStatut(StatutMission.CLOTUREE);
                return mission;
            });

        var result = missionService.cloturer(12L);

        assertThat(result.getStatut()).isEqualTo(StatutMission.CLOTUREE);
        assertThat(mission.getDateCloture()).isNotNull();
        assertThat(mission.getEstEnCarence()).isFalse();
        verify(notificationCenterService).publish(
            eq("MISSION_CLOTUREE"),
            eq("Mission clôturée"),
            contains("SIN-12"),
            eq("/missions/12")
        );
    }

    @Test
    void sortirDeCarence_resetsCarenceFields() {
        Mission mission = createMission(13L, "SIN-13", StatutMission.CARENCE);
        mission.setEstEnCarence(true);
        mission.setDateCarence(LocalDateTime.now().minusHours(12));
        mission.setDureeCarenceHeures(12);
        User actor = createUser(7L, "expert3@wafa.ma", "Expert", "Three");

        when(missionRepository.findById(13L)).thenReturn(Optional.of(mission));
        when(currentUserService.requireCurrentUser()).thenReturn(actor);
        when(missionStateMachineService.transition(eq(mission), eq(StatutMission.EN_COURS), eq(actor), contains("Sortie manuelle de carence")))
            .thenAnswer(invocation -> {
                mission.setStatut(StatutMission.EN_COURS);
                return mission;
            });

        var result = missionService.sortirDeCarence(13L);

        assertThat(result.getStatut()).isEqualTo(StatutMission.EN_COURS);
        assertThat(mission.getEstEnCarence()).isFalse();
        assertThat(mission.getDateCarence()).isNull();
        assertThat(mission.getDureeCarenceHeures()).isZero();
    }

    @Test
    void refuser_requiresCommentForAutre() {
        Mission mission = createMission(14L, "SIN-14", StatutMission.NOUVELLE);
        User expert = createUser(6L, "expert4@wafa.ma", "Expert", "Four");
        RefusRequest request = new RefusRequest();
        request.setMotif(MotifRefus.AUTRE);
        request.setCommentaire("   ");

        when(missionRepository.findById(14L)).thenReturn(Optional.of(mission));
        when(currentUserService.requireCurrentUser()).thenReturn(expert);

        assertThatThrownBy(() -> missionService.refuser(14L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("commentaire est obligatoire");
    }

    private Mission createMission(Long id, String refSinistre, StatutMission statut) {
        Mission mission = new Mission();
        mission.setId(id);
        mission.setRefSinistre(refSinistre);
        mission.setNumPolice("POL-001");
        mission.setParcours("Express");
        mission.setDateCreation(LocalDateTime.now().minusDays(1));
        mission.setStatut(statut);
        mission.setEstEnCarence(false);
        mission.setDureeCarenceHeures(0);
        return mission;
    }

    private User createUser(Long id, String email, String prenom, String nom) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPrenom(prenom);
        user.setNom(nom);
        return user;
    }
}