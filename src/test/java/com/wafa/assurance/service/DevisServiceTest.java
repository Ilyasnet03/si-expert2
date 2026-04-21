package com.wafa.assurance.service;

import com.wafa.assurance.dto.AccordDevisRequest;
import com.wafa.assurance.dto.DevisRequest;
import com.wafa.assurance.model.Devis;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.StatutDevis;
import com.wafa.assurance.model.TypeDevis;
import com.wafa.assurance.model.TypeOperation;
import com.wafa.assurance.repository.DevisRepository;
import com.wafa.assurance.repository.MissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DevisServiceTest {

    @Mock
    private DevisRepository devisRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private NotificationCenterService notificationCenterService;

    @InjectMocks
    private DevisService devisService;

    @Test
    void findByMission_mapsEntitiesToDtos() {
        Mission mission = createMission(31L, "SIN-31");
        Devis devis = createDevis(40L, mission);

        when(devisRepository.findByMissionIdOrderByDateCreationDesc(31L)).thenReturn(List.of(devis));

        var result = devisService.findByMission(31L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(40L);
        assertThat(result.get(0).getGarage()).isEqualTo("Garage Atlas");
        assertThat(result.get(0).getMontantTotal()).isEqualByComparingTo("3500.00");
    }

    @Test
    void create_withoutImage_savesPendingDevisAndPublishesNotification() throws IOException {
        Mission mission = createMission(32L, "SIN-32");
        DevisRequest request = new DevisRequest();
        request.setGarage("Garage Atlas");
        request.setTypeDevis("INITIAL");
        request.setMontantPieces(new BigDecimal("1200.00"));
        request.setMontantPeinture(new BigDecimal("700.00"));
        request.setMontantMainOeuvre(new BigDecimal("900.00"));
        request.setMontantTotal(new BigDecimal("2800.00"));
        request.setTypeOperation("REPARATION");
        request.setExpertiseContradictoire(true);
        request.setObservations("Remplacement aile avant");

        when(missionRepository.findById(32L)).thenReturn(Optional.of(mission));
        when(devisRepository.save(any(Devis.class))).thenAnswer(invocation -> {
            Devis saved = invocation.getArgument(0);
            saved.setId(41L);
            saved.setDateCreation(LocalDateTime.now());
            return saved;
        });

        var result = devisService.create(32L, request, null);

        assertThat(result.getId()).isEqualTo(41L);
        assertThat(result.getStatut()).isEqualTo(StatutDevis.EN_ATTENTE);
        assertThat(result.getGarage()).isEqualTo("Garage Atlas");

        ArgumentCaptor<Devis> devisCaptor = ArgumentCaptor.forClass(Devis.class);
        verify(devisRepository).save(devisCaptor.capture());
        Devis savedDevis = devisCaptor.getValue();
        assertThat(savedDevis.getMission()).isEqualTo(mission);
        assertThat(savedDevis.getTypeDevis()).isEqualTo(TypeDevis.INITIAL);
        assertThat(savedDevis.getTypeOperation()).isEqualTo(TypeOperation.REPARATION);
        assertThat(savedDevis.getDemandeExpertiseContradictoire()).isTrue();
        assertThat(savedDevis.getCheminImage()).isNull();

        verify(notificationCenterService).publish(
            eq("DEVIS_RECU"),
            eq("Réception du devis de réparation"),
            contains("SIN-32"),
            eq("/missions/32/devis")
        );
    }

    @Test
    void accorder_updatesAwardedAmountsAndStatus() {
        Mission mission = createMission(33L, "SIN-33");
        Devis devis = createDevis(42L, mission);
        AccordDevisRequest request = new AccordDevisRequest();
        request.setMontantAccordePieces(new BigDecimal("1000.00"));
        request.setMontantAccordePeinture(new BigDecimal("800.00"));
        request.setMontantAccordeMainOeuvre(new BigDecimal("600.00"));
        request.setTypeOperationAccorde("REMPLACEMENT");
        request.setObservations("Accord partiel validé");

        when(devisRepository.findById(42L)).thenReturn(Optional.of(devis));
        when(devisRepository.save(devis)).thenReturn(devis);

        var result = devisService.accorder(42L, request);

        assertThat(result.getStatut()).isEqualTo(StatutDevis.ACCORDE);
        assertThat(devis.getMontantPiecesAccorde()).isEqualByComparingTo("1000.00");
        assertThat(devis.getMontantPeintureAccorde()).isEqualByComparingTo("800.00");
        assertThat(devis.getMontantMainOeuvreAccorde()).isEqualByComparingTo("600.00");
        assertThat(devis.getTypeOperationAccorde()).isEqualTo(TypeOperation.REMPLACEMENT);
        assertThat(devis.getObservations()).isEqualTo("Accord partiel validé");

        verify(notificationCenterService).publish(
            eq("DEVIS_ACC42"),
            eq("Devis accordé"),
            contains("Garage Atlas"),
            eq("/missions/33/devis")
        );
    }

    @Test
    void delete_removesStoredFileAndRepositoryEntry() throws IOException {
        Mission mission = createMission(34L, "SIN-34");
        Devis devis = createDevis(43L, mission);
        Path tempFile = Files.createTempFile("devis-service-test", ".jpg");
        devis.setCheminImage(tempFile.toString());

        try {
            when(devisRepository.findById(43L)).thenReturn(Optional.of(devis));

            devisService.delete(43L);

            assertThat(Files.exists(tempFile)).isFalse();
            verify(devisRepository).deleteById(43L);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private Mission createMission(Long id, String refSinistre) {
        Mission mission = new Mission();
        mission.setId(id);
        mission.setRefSinistre(refSinistre);
        return mission;
    }

    private Devis createDevis(Long id, Mission mission) {
        Devis devis = new Devis();
        devis.setId(id);
        devis.setMission(mission);
        devis.setGarage("Garage Atlas");
        devis.setTypeDevis(TypeDevis.INITIAL);
        devis.setTypeOperation(TypeOperation.REPARATION);
        devis.setMontantPieces(new BigDecimal("1500.00"));
        devis.setMontantPeinture(new BigDecimal("1000.00"));
        devis.setMontantMainOeuvre(new BigDecimal("1000.00"));
        devis.setMontantTotal(new BigDecimal("3500.00"));
        devis.setStatut(StatutDevis.EN_ATTENTE);
        devis.setDateCreation(LocalDateTime.now());
        return devis;
    }
}