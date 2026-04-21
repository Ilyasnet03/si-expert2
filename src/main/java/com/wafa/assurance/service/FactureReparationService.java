package com.wafa.assurance.service;

import com.wafa.assurance.dto.FactureReparationDTO;
import com.wafa.assurance.dto.FactureReparationRequest;
import com.wafa.assurance.model.Devis;
import com.wafa.assurance.model.FactureReparation;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.StatutPaiementFacture;
import com.wafa.assurance.repository.DevisRepository;
import com.wafa.assurance.repository.FactureReparationRepository;
import com.wafa.assurance.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FactureReparationService {

    @Autowired
    private FactureReparationRepository factureRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private DevisRepository devisRepository;

    @Autowired
    private NotificationCenterService notificationCenterService;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public List<FactureReparationDTO> findByMission(Long missionId) {
        return factureRepository.findByMissionIdOrderByDateCreationDesc(missionId)
            .stream()
            .map(FactureReparationDTO::fromEntity)
            .toList();
    }

    public FactureReparationDTO create(Long missionId, FactureReparationRequest req, MultipartFile fichier) throws IOException {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + missionId));

        FactureReparation facture = new FactureReparation();
        facture.setMission(mission);
        apply(facture, req);
        if (fichier != null && !fichier.isEmpty()) {
            saveFile(missionId, facture, fichier);
        }

        FactureReparation saved = factureRepository.save(facture);
        notificationCenterService.publish(
            "FACTURE_REPARATION",
            "Réception d'une facture de réparation",
            "Facture " + saved.getNumeroFacture() + " enregistrée pour la mission " + mission.getRefSinistre(),
            "/missions/" + missionId + "/factures"
        );
        return FactureReparationDTO.fromEntity(saved);
    }

    public FactureReparationDTO update(Long factureId, FactureReparationRequest req) {
        FactureReparation facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new RuntimeException("Facture non trouvée: " + factureId));
        apply(facture, req);
        return FactureReparationDTO.fromEntity(factureRepository.save(facture));
    }

    public Resource download(Long factureId) throws IOException {
        FactureReparation facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new RuntimeException("Facture non trouvée: " + factureId));
        if (facture.getCheminFichier() == null) {
            throw new FileNotFoundException("Aucun fichier associé à cette facture");
        }

        Resource resource = new FileSystemResource(facture.getCheminFichier());
        if (!resource.exists()) {
            throw new FileNotFoundException("Fichier introuvable");
        }
        return resource;
    }

    public void delete(Long factureId) throws IOException {
        FactureReparation facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new RuntimeException("Facture non trouvée: " + factureId));
        if (facture.getCheminFichier() != null) {
            Files.deleteIfExists(Paths.get(facture.getCheminFichier()));
        }
        factureRepository.delete(facture);
    }

    private void apply(FactureReparation facture, FactureReparationRequest req) {
        facture.setNumeroFacture(req.getNumeroFacture());
        facture.setDateFacture(req.getDateFacture());
        facture.setMontantHT(req.getMontantHT());

        BigDecimal montantTVA = req.getMontantTVA() != null
            ? req.getMontantTVA()
            : req.getMontantHT().multiply(new BigDecimal("0.2"));
        facture.setMontantTVA(montantTVA);

        BigDecimal montantTTC = req.getMontantTTC() != null
            ? req.getMontantTTC()
            : req.getMontantHT().add(montantTVA);
        facture.setMontantTTC(montantTTC);
        facture.setGarageEmetteur(req.getGarageEmetteur());
        facture.setStatutPaiement(req.getStatutPaiement() != null ? StatutPaiementFacture.valueOf(req.getStatutPaiement()) : StatutPaiementFacture.EN_ATTENTE);
        facture.setDevis(resolveDevis(req.getDevisId()));
    }

    private Devis resolveDevis(Long devisId) {
        if (devisId == null) {
            return null;
        }
        return devisRepository.findById(devisId).orElse(null);
    }

    private void saveFile(Long missionId, FactureReparation facture, MultipartFile fichier) throws IOException {
        Path dir = Paths.get(uploadDir, "missions", missionId.toString(), "factures");
        Files.createDirectories(dir);
        String extension = getFileExtension(fichier.getOriginalFilename());
        String fileName = UUID.randomUUID() + "." + extension;
        Path destination = dir.resolve(fileName);
        Files.copy(fichier.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        facture.setCheminFichier(destination.toString());
        facture.setNomFichier(fichier.getOriginalFilename());
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "pdf";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}