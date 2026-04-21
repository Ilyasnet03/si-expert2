package com.wafa.assurance.service;

import com.wafa.assurance.model.StatutMission;
import com.wafa.assurance.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissionCarenceScheduler {

    private final MissionRepository missionRepository;
    private final MissionService missionService;

    @Value("${app.carence.threshold-hours:48}")
    private int seuilCarenceHeures;

    @Scheduled(cron = "0 0 * * * *")
    public void calculerCarenceToutesLesHeures() {
        List<StatutMission> statutsEligibles = List.of(
            StatutMission.ACCEPTEE,
            StatutMission.NON_CLOTUREE,
            StatutMission.EN_COURS,
            StatutMission.REEXAMEN
        );

        int total = missionRepository.findEligibleForCarence(statutsEligibles)
            .stream()
            .mapToInt(mission -> missionService.appliquerCarence(mission.getId(), seuilCarenceHeures))
            .sum();

        if (total > 0) {
            log.info("{} mission(s) passées en carence.", total);
        }
    }
}