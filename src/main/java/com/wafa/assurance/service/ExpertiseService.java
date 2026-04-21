package com.wafa.assurance.service;

import com.wafa.assurance.dto.ExpertiseDTO;
import com.wafa.assurance.dto.ExpertiseRequest;
import com.wafa.assurance.dto.VvadeRequest;
import com.wafa.assurance.dto.VvadeResponse;
import com.wafa.assurance.model.Expertise;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.repository.ExpertiseRepository;
import com.wafa.assurance.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ExpertiseService {

    @Autowired
    private ExpertiseRepository expertiseRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private NotificationCenterService notificationCenterService;

    @Autowired
    private VvadeCalculator vvadeCalculator;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public ExpertiseDTO getOrCreateByMission(Long missionId) {
        List<Expertise> expertises = expertiseRepository.findByMissionIdOrderByDateExpertiseDesc(missionId);
        
        if (!expertises.isEmpty()) {
            return ExpertiseDTO.fromEntity(expertises.get(0));
        }
        
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + missionId));
        
        Expertise expertise = new Expertise();
        expertise.setMission(mission);
        Expertise saved = expertiseRepository.save(expertise);
        return ExpertiseDTO.from(saved);
    }

    public ExpertiseDTO update(Long missionId, ExpertiseRequest req) {
        List<Expertise> expertises = expertiseRepository.findByMissionIdOrderByDateExpertiseDesc(missionId);
        
        Expertise expertise;
        if (!expertises.isEmpty()) {
            expertise = expertises.get(0);
        } else {
            Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + missionId));
            expertise = new Expertise();
            expertise.setMission(mission);
        }
        
        if (req.getDateExpertise() != null) {
            expertise.setDateExpertise(req.getDateExpertise().atStartOfDay());
        }
        if (req.getVille() != null) {
            expertise.setVille(req.getVille());
        }
        if (req.getDateMiseEnCirculation() != null) {
            expertise.setDateMiseEnCirculation(req.getDateMiseEnCirculation());
        }
        if (req.getAdresse() != null) {
            expertise.setAdresse(req.getAdresse());
        }
        if (req.getKilometrage() != null) {
            expertise.setKilometrage(req.getKilometrage());
        }
        if (req.getEtatGeneral() != null) {
            expertise.setEtatGeneral(req.getEtatGeneral());
        }
        if (req.getEtatVehicule() != null) {
            expertise.setEtatVehicule(req.getEtatVehicule());
        }
        if (req.getEstimationDommages() != null) {
            expertise.setEstimationDommages(req.getEstimationDommages());
        }
        if (req.getMontantEstimation() != null) {
            expertise.setMontantEstimation(req.getMontantEstimation());
        }
        if (req.getEntretien() != null) {
            expertise.setEntretien(req.getEntretien());
        }
        if (req.getCarnetEntretien() != null) {
            expertise.setCarnetEntretien(req.getCarnetEntretien());
        }
        if (req.getCarnetEntretienPresent() != null) {
            expertise.setCarnetEntretienPresent(req.getCarnetEntretienPresent());
        }
        if (req.getOptionsSpecifiques() != null) {
            expertise.setOptionsSpecifiques(req.getOptionsSpecifiques());
        }
        if (req.getSinistresAnterieurs() != null) {
            expertise.setSinistresAnterieurs(req.getSinistresAnterieurs());
        }
        if (req.getCoteArgus() != null) {
            expertise.setCoteArgus(req.getCoteArgus());
        }
        if (req.getCalculVVADE() != null) {
            expertise.setCalculVVADE(req.getCalculVVADE());
        }
        if (req.getReforme() != null) {
            expertise.setReforme(Boolean.TRUE.equals(req.getReforme()) ? req.getExplicationReforme() : null);
        }
        if (req.getDateDemandeContreExpertise() != null) {
            expertise.setDateDemandeContreExpertise(req.getDateDemandeContreExpertise().atStartOfDay());
        }
        if (req.getExpertAdverseDesigne() != null) {
            expertise.setExpertAdverseDesigne(req.getExpertAdverseDesigne());
        }
        if (req.getDateDesignationExpertAdverse() != null) {
            expertise.setDateDesignationExpertAdverse(req.getDateDesignationExpertAdverse().atStartOfDay());
        }
        if (req.getDateExpertiseAdverse() != null) {
            expertise.setDateExpertiseAdverse(req.getDateExpertiseAdverse().atStartOfDay());
        }
        if (req.getAccordExperts() != null) {
            expertise.setAccordExperts(req.getAccordExperts());
        }
        if (req.getDateDemandeArbitrage() != null) {
            expertise.setDateDemandeArbitrage(req.getDateDemandeArbitrage().atStartOfDay());
        }
        if (req.getDateDesignationArbitre() != null) {
            expertise.setDateDesignationArbitre(req.getDateDesignationArbitre().atStartOfDay());
        }
        if (req.getExpertArbitreDesigne() != null) {
            expertise.setExpertArbitreDesigne(req.getExpertArbitreDesigne());
        }
        if (req.getDateExpertiseArbitrale() != null) {
            expertise.setDateExpertiseArbitrale(req.getDateExpertiseArbitrale().atStartOfDay());
        }
        if (req.getRapportDefinitif() != null) {
            expertise.setRapportDefinitif(req.getRapportDefinitif());
        }
        if (req.getObservations() != null) {
            expertise.setObservations(req.getObservations());
        }
        
        Expertise updated = expertiseRepository.save(expertise);
        notificationCenterService.publish(
            "EXPERTISE",
            "Expertise mise à jour",
            "Le dossier d'expertise de la mission " + updated.getMission().getRefSinistre() + " a été mis à jour.",
            "/missions/" + missionId + "/expertise"
        );
        return ExpertiseDTO.fromEntity(updated);
    }

    public ExpertiseDTO update(Long missionId, ExpertiseRequest req, MultipartFile carnetEntretien, MultipartFile rapportDefinitif) throws IOException {
        if (carnetEntretien != null && !carnetEntretien.isEmpty()) {
            req.setCarnetEntretien(saveFile(missionId, carnetEntretien, "expertise", "carnet"));
        }
        if (rapportDefinitif != null && !rapportDefinitif.isEmpty()) {
            req.setRapportDefinitif(saveFile(missionId, rapportDefinitif, "expertise", "rapport"));
        }
        return update(missionId, req);
    }

    public void delete(Long id) {
        expertiseRepository.deleteById(id);
    }

    public VvadeResponse calculerVvade(Long missionId, VvadeRequest request) {
        List<Expertise> expertises = expertiseRepository.findByMissionIdOrderByDateExpertiseDesc(missionId);
        Expertise expertise = expertises.isEmpty() ? null : expertises.get(0);

        VvadeRequest effectiveRequest = request;
        if (expertise != null) {
            if (effectiveRequest.getDateMiseEnCirculation() == null) {
                effectiveRequest.setDateMiseEnCirculation(expertise.getDateMiseEnCirculation());
            }
            if (effectiveRequest.getKilometrage() == null) {
                effectiveRequest.setKilometrage(expertise.getKilometrage());
            }
            if (effectiveRequest.getEtatGeneral() == null) {
                effectiveRequest.setEtatGeneral(expertise.getEtatGeneral());
            }
            if (effectiveRequest.getCarnetEntretienPresent() == null) {
                effectiveRequest.setCarnetEntretienPresent(expertise.getCarnetEntretienPresent());
            }
            if (effectiveRequest.getOptionsSpecifiques() == null) {
                effectiveRequest.setOptionsSpecifiques(expertise.getOptionsSpecifiques());
            }
            if (effectiveRequest.getSinistresAnterieurs() == null) {
                effectiveRequest.setSinistresAnterieurs(expertise.getSinistresAnterieurs());
            }
            if (effectiveRequest.getCoteArgus() == null) {
                effectiveRequest.setCoteArgus(expertise.getCoteArgus());
            }
        }

        var coteArgus = vvadeCalculator.getCoteArgus(effectiveRequest);
        var vvade = vvadeCalculator.calculer(effectiveRequest);

        if (expertise != null) {
            expertise.setCoteArgus(coteArgus);
            expertise.setCalculVVADE(vvade.toPlainString());
            expertiseRepository.save(expertise);
        }

        return new VvadeResponse(coteArgus, vvade, "Base Argus x coefficients kilométrage, état, entretien et antécédents.");
    }

    private String saveFile(Long missionId, MultipartFile file, String folder, String prefix) throws IOException {
        Path dir = Paths.get(uploadDir, "missions", missionId.toString(), folder);
        Files.createDirectories(dir);
        String extension = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
            ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1)
            : "bin";
        Path destination = dir.resolve(prefix + "-" + UUID.randomUUID() + "." + extension);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }
}
