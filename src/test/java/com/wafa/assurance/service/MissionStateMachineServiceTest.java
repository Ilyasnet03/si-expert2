package com.wafa.assurance.service;

import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.MissionTransition;
import com.wafa.assurance.model.StatutMission;
import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.MissionRepository;
import com.wafa.assurance.repository.MissionTransitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionStateMachineServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MissionTransitionRepository missionTransitionRepository;

    @InjectMocks
    private MissionStateMachineService service;

    @Test
    void transition_updatesMissionAndStoresAuditTrailForAllowedTransition() {
        Mission mission = new Mission();
        mission.setStatut(StatutMission.NON_CLOTUREE);

        User actor = new User();
        actor.setId(7L);
        actor.setPrenom("Ilyas");
        actor.setNom("Test");
        actor.setRole("EXPERT");

        when(missionRepository.save(mission)).thenReturn(mission);
        when(missionTransitionRepository.save(any(MissionTransition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mission result = service.transition(mission, StatutMission.HONORAIRES, actor, "Passage a la facturation");

        assertThat(result.getStatut()).isEqualTo(StatutMission.HONORAIRES);

        ArgumentCaptor<MissionTransition> captor = ArgumentCaptor.forClass(MissionTransition.class);
        verify(missionTransitionRepository).save(captor.capture());
        MissionTransition transition = captor.getValue();

        assertThat(transition.getAncienStatut()).isEqualTo(StatutMission.NON_CLOTUREE);
        assertThat(transition.getNouveauStatut()).isEqualTo(StatutMission.HONORAIRES);
        assertThat(transition.getActeurId()).isEqualTo(7L);
        assertThat(transition.getActeurNom()).isEqualTo("Ilyas Test");
        assertThat(transition.getActeurRole()).isEqualTo("EXPERT");
        assertThat(transition.getCommentaire()).isEqualTo("Passage a la facturation");
    }

    @Test
    void transition_usesSystemActorWhenNoActorIsProvided() {
        Mission mission = new Mission();
        mission.setStatut(StatutMission.CLOTUREE);

        when(missionRepository.save(mission)).thenReturn(mission);

        service.transition(mission, StatutMission.REEXAMEN, null, "Reouverture automatique");

        ArgumentCaptor<MissionTransition> captor = ArgumentCaptor.forClass(MissionTransition.class);
        verify(missionTransitionRepository).save(captor.capture());

        assertThat(captor.getValue().getActeurNom()).isEqualTo("Système");
        assertThat(captor.getValue().getActeurRole()).isEqualTo("SYSTEM");
    }

    @Test
    void transition_rejectsForbiddenTransition() {
        Mission mission = new Mission();
        mission.setStatut(StatutMission.NOUVELLE);

        assertThatThrownBy(() -> service.transition(mission, StatutMission.HONORAIRES, null, "Interdit"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Transition interdite");
    }
}