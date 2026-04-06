package com.wafa.assurance.service;

import com.wafa.assurance.dto.ExpertiseDTO;
import com.wafa.assurance.dto.ExpertiseRequest;
import com.wafa.assurance.model.Expertise;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.repository.ExpertiseRepository;
import com.wafa.assurance.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpertiseService {

    @Autowired
    private ExpertiseRepository expertiseRepository;

    @Autowired
    private MissionRepository missionRepository;

    public ExpertiseDTO getOrCreateByMission(Long missionId) {
        List<Expertise> expertises = expertiseRepository.findByMissionIdOrderByCreatedAtDesc(missionId);
        
        if (!expertises.isEmpty()) {
            return ExpertiseDTO.from(expertises.get(0));
        }
        
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + missionId));
        
        Expertise expertise = new Expertise();
        expertise.setMission(mission);
        Expertise saved = expertiseRepository.save(expertise);
        return ExpertiseDTO.from(saved);
    }

    public ExpertiseDTO update(Long missionId, ExpertiseRequest req) {
        List<Expertise> expertises = expertiseRepository.findByMissionIdOrderByCreatedAtDesc(missionId);
        
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
            expertise.setDateExpertise(req.getDateExpertise());
        }
        if (req.getLieu() != null) {
            expertise.setLieu(req.getLieu());
        }
        if (req.getKilometrage() != null) {
            expertise.setKilometrage(req.getKilometrage());
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
        if (req.getCalculVVADE() != null) {
            expertise.setCalculVVADE(req.getCalculVVADE());
        }
        if (req.getArbitrage() != null) {
            expertise.setArbitrage(req.getArbitrage());
        }
        if (req.getExpertiseContradictoire() != null) {
            expertise.setExpertiseContradictoire(req.getExpertiseContradictoire());
        }
        if (req.getDateExpertiseContradictoire() != null) {
            expertise.setDateExpertiseContradictoire(req.getDateExpertiseContradictoire());
        }
        if (req.getMontantExpertiseContradictoire() != null) {
            expertise.setMontantExpertiseContradictoire(req.getMontantExpertiseContradictoire());
        }
        if (req.getObservations() != null) {
            expertise.setObservations(req.getObservations());
        }
        
        Expertise updated = expertiseRepository.save(expertise);
        return ExpertiseDTO.from(updated);
    }

    public void delete(Long id) {
        expertiseRepository.deleteById(id);
    }
}
