package com.wafa.assurance.service;

import com.wafa.assurance.dto.AccordDevisRequest;
import com.wafa.assurance.dto.DevisDTO;
import com.wafa.assurance.dto.DevisRequest;
import com.wafa.assurance.model.Devis;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.StatutDevis;
import com.wafa.assurance.model.TypeDevis;
import com.wafa.assurance.model.TypeOperation;
import com.wafa.assurance.repository.DevisRepository;
import com.wafa.assurance.repository.MissionRepository;
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
import java.util.stream.Collectors;

@Service
public class DevisService {

    @Autowired
    private DevisRepository devisRepository;

    @Autowired
    private MissionRepository missionRepository;

    private static final String UPLOAD_DIR = "./uploads";

    public List<DevisDTO> findByMission(Long missionId) {
        return devisRepository.findByMissionIdOrderByCreatedAtDesc(missionId)
            .stream()
            .map(DevisDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public DevisDTO create(Long missionId, DevisRequest req, MultipartFile image) throws IOException {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + missionId));

        Devis devis = new Devis();
        devis.setMission(mission);
        devis.setGarage(req.getGarage());
        devis.setTypeDevis(TypeDevis.valueOf(req.getTypeDevis()));
        devis.setMontantPieces(req.getMontantPieces());
        devis.setMontantPeinture(req.getMontantPeinture());
        devis.setMontantMainOeuvre(req.getMontantMainOeuvre());
        devis.setMontantTotal(req.getMontantTotal());
        devis.setTypeOperation(TypeOperation.valueOf(req.getTypeOperation()));
        devis.setExpertiseContradictoire(req.isExpertiseContradictoire());
        devis.setObservations(req.getObservations());
        devis.setStatut(StatutDevis.EN_ATTENTE);

        // Upload image si fournie
        if (image != null && !image.isEmpty()) {
            String cheminImage = sauvegarderImage(missionId, image);
            devis.setCheminImage(cheminImage);
        }

        Devis saved = devisRepository.save(devis);
        return DevisDTO.fromEntity(saved);
    }

    public DevisDTO accorder(Long devisId, AccordDevisRequest req) {
        Devis devis = devisRepository.findById(devisId)
            .orElseThrow(() -> new RuntimeException("Devis non trouvé: " + devisId));

        devis.setMontantAccordePieces(req.getMontantAccordePieces());
        devis.setMontantAccordePeinture(req.getMontantAccordePeinture());
        devis.setMontantAccordeMainOeuvre(req.getMontantAccordeMainOeuvre());
        devis.setMontantAccordeTotal(req.getMontantAccordeTotal());
        
        if (req.getObservations() != null) {
            devis.setObservations(req.getObservations());
        }
        
        devis.setStatut(StatutDevis.ACCORDE);

        Devis updated = devisRepository.save(devis);
        return DevisDTO.fromEntity(updated);
    }

    public void delete(Long devisId) throws IOException {
        Devis devis = devisRepository.findById(devisId)
            .orElseThrow(() -> new RuntimeException("Devis non trouvé"));

        if (devis.getCheminImage() != null) {
            try {
                Files.deleteIfExists(Paths.get(devis.getCheminImage()));
            } catch (IOException e) {
                // Log mais ne pas bloquer la suppression
                System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            }
        }

        devisRepository.deleteById(devisId);
    }

    private String sauvegarderImage(Long missionId, MultipartFile file) throws IOException {
        Path dir = Paths.get(UPLOAD_DIR, "missions", missionId.toString(), "devis");
        Files.createDirectories(dir);

        String ext = getFileExtension(file.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + "." + ext;
        Path destination = dir.resolve(nomFichier);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return destination.toString();
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") < 0) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
