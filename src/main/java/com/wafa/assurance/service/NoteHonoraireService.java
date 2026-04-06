package com.wafa.assurance.service;

import com.wafa.assurance.dto.NoteHonoraireDTO;
import com.wafa.assurance.dto.NoteHonoraireRequest;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.NoteHonoraire;
import com.wafa.assurance.repository.MissionRepository;
import com.wafa.assurance.repository.NoteHonoraireRepository;
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
import java.util.stream.Collectors;

@Service
public class NoteHonoraireService {

    @Autowired
    private NoteHonoraireRepository noteHonoraireRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public List<NoteHonoraireDTO> findByMission(Long missionId) {
        return noteHonoraireRepository.findByMissionIdOrderByCreatedAtDesc(missionId)
            .stream()
            .map(NoteHonoraireDTO::from)
            .collect(Collectors.toList());
    }

    public NoteHonoraireDTO create(Long missionId, NoteHonoraireRequest req, MultipartFile fichier) throws IOException {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + missionId));

        // Vérifier l'unicité du numéro de note
        if (noteHonoraireRepository.findByNumeroNote(req.getNumeroNote()).isPresent()) {
            throw new RuntimeException("Ce numéro de note existe déjà: " + req.getNumeroNote());
        }

        NoteHonoraire noteHonoraire = new NoteHonoraire();
        noteHonoraire.setMission(mission);
        noteHonoraire.setNumeroNote(req.getNumeroNote());
        noteHonoraire.setDescription(req.getDescription());
        noteHonoraire.setMontantHT(req.getMontantHT());
        
        // Calculer le montant TTC
        BigDecimal tauxTVA = req.getTauxTVA() != null ? req.getTauxTVA() : new BigDecimal("20.00");
        noteHonoraire.setTauxTVA(tauxTVA);
        
        BigDecimal montantTVA = req.getMontantHT().multiply(tauxTVA).divide(new BigDecimal("100"));
        noteHonoraire.setMontantTVA(montantTVA);
        
        BigDecimal montantTTC = req.getMontantHT().add(montantTVA);
        noteHonoraire.setMontantTTC(montantTTC);
        
        noteHonoraire.setObservations(req.getObservations());

        // Upload fichier PDF si fourni
        if (fichier != null && !fichier.isEmpty()) {
            String cheminFichier = sauvegarderFichier(missionId, fichier);
            noteHonoraire.setCheminFichier(cheminFichier);
            noteHonoraire.setNomFichier(fichier.getOriginalFilename());
        }

        NoteHonoraire saved = noteHonoraireRepository.save(noteHonoraire);
        return NoteHonoraireDTO.from(saved);
    }

    public NoteHonoraireDTO update(Long id, NoteHonoraireRequest req) {
        NoteHonoraire noteHonoraire = noteHonoraireRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Note d'honoraire non trouvée: " + id));

        if (req.getDescription() != null) {
            noteHonoraire.setDescription(req.getDescription());
        }
        if (req.getMontantHT() != null) {
            noteHonoraire.setMontantHT(req.getMontantHT());
            // Recalculer les montants
            BigDecimal tauxTVA = req.getTauxTVA() != null ? req.getTauxTVA() : noteHonoraire.getTauxTVA();
            BigDecimal montantTVA = req.getMontantHT().multiply(tauxTVA).divide(new BigDecimal("100"));
            noteHonoraire.setMontantTVA(montantTVA);
            noteHonoraire.setMontantTTC(req.getMontantHT().add(montantTVA));
        }
        if (req.getObservations() != null) {
            noteHonoraire.setObservations(req.getObservations());
        }

        NoteHonoraire updated = noteHonoraireRepository.save(noteHonoraire);
        return NoteHonoraireDTO.from(updated);
    }

    public Resource telecharger(Long noteId) throws IOException {
        NoteHonoraire note = noteHonoraireRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("Note d'honoraire non trouvée: " + noteId));

        if (note.getCheminFichier() == null) {
            throw new FileNotFoundException("Aucun fichier associé à cette note");
        }

        Resource resource = new FileSystemResource(note.getCheminFichier());
        if (!resource.exists()) {
            throw new FileNotFoundException("Fichier introuvable: " + note.getCheminFichier());
        }
        return resource;
    }

    public void delete(Long id) throws IOException {
        NoteHonoraire note = noteHonoraireRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Note d'honoraire non trouvée"));

        if (note.getCheminFichier() != null) {
            try {
                Files.deleteIfExists(Paths.get(note.getCheminFichier()));
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            }
        }

        noteHonoraireRepository.deleteById(id);
    }

    private String sauvegarderFichier(Long missionId, MultipartFile fichier) throws IOException {
        Path dir = Paths.get(uploadDir, "missions", missionId.toString(), "honoraires");
        Files.createDirectories(dir);

        String nomFichier = UUID.randomUUID() + ".pdf";
        Path destination = dir.resolve(nomFichier);

        Files.copy(fichier.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }
}
