package com.wafa.assurance.service;

import com.wafa.assurance.dto.MissionDTO;
import com.wafa.assurance.dto.MissionRefusDTO;
import com.wafa.assurance.dto.MissionTransitionDTO;
import com.wafa.assurance.dto.MissionReouvertureRequest;
import com.wafa.assurance.dto.PhotoDTO;
import com.wafa.assurance.dto.DevisDTO;
import com.wafa.assurance.dto.ExpertiseDTO;
import com.wafa.assurance.dto.NoteHonoraireDTO;
import com.wafa.assurance.dto.RefusRequest;
import com.wafa.assurance.model.*;
import com.wafa.assurance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private DevisRepository devisRepository;

    @Autowired
    private ExpertiseRepository expertiseRepository;

    @Autowired
    private NoteHonoraireRepository noteHonoraireRepository;

    @Autowired
    private NotificationCenterService notificationCenterService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private MissionStateMachineService missionStateMachineService;

    @Autowired
    private MissionRefusRepository missionRefusRepository;

    @Autowired
    private MissionTransitionRepository missionTransitionRepository;

    @Autowired
    private MissionReouvertureRepository missionReouvertureRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<MissionDTO> search(
            String refSinistre,
            String numPolice,
            String parcours,
            StatutMission statut,
            LocalDate dateDebut,
            LocalDate dateFin,
            Pageable pageable) {
        
        List<Mission> missions = missionRepository.findAll();
        
        // Filtrer les résultats
        missions = missions.stream()
            .filter(m -> refSinistre == null || m.getRefSinistre().contains(refSinistre))
            .filter(m -> numPolice == null || m.getNumPolice().contains(numPolice))
            .filter(m -> parcours == null || m.getParcours().contains(parcours))
            .filter(m -> statut == null || m.getStatut() == statut)
            .filter(m -> {
                if (dateDebut == null && dateFin == null) return true;
                LocalDateTime createdAt = m.getDateCreation();
                if (createdAt == null) return true;
                
                if (dateDebut != null && createdAt.toLocalDate().isBefore(dateDebut)) return false;
                if (dateFin != null && createdAt.toLocalDate().isAfter(dateFin)) return false;
                return true;
            })
            .collect(Collectors.toList());

        // Convertir en DTOs et paginer
        List<MissionDTO> dtos = missions.stream()
            .map(MissionDTO::fromEntity)
            .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        
        return new PageImpl<>(
            dtos.subList(start, end),
            pageable,
            dtos.size()
        );
    }

    public MissionDTO findById(Long id) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + id));
        return MissionDTO.fromEntity(mission);
    }

    public MissionDTO accepter(Long id, boolean signalerInvestigation) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        User expertCourant = currentUserService.requireCurrentUser();
        
        mission.setExpert(expertCourant);
        mission.setDateAffectation(LocalDateTime.now());
        mission.setEstEnCarence(false);
        mission.setDateCarence(null);
        mission.setDureeCarenceHeures(0);
        
        if (signalerInvestigation) {
            mission.setObservations((mission.getObservations() != null ? mission.getObservations() + "\n" : "")
                + "[Investigation signalée " + LocalDateTime.now() + "]");
        }
        
        Mission updated = missionStateMachineService.transition(
            mission,
            StatutMission.NON_CLOTUREE,
            expertCourant,
            signalerInvestigation ? "Mission acceptée avec signalement d'investigation." : "Mission acceptée par l'expert."
        );
        notificationCenterService.publish(
            "MISSION_ASSIGNEE",
            "Nouvelle mission assignée",
            "La mission " + updated.getRefSinistre() + " a été acceptée par " + expertCourant.getPrenom() + " " + expertCourant.getNom() + ".",
            "/missions/" + updated.getId()
        );
        return MissionDTO.fromEntity(updated);
    }

    public MissionDTO refuser(Long id, RefusRequest request) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        User expertCourant = currentUserService.requireCurrentUser();

        if (request.getMotif() == null) {
            throw new IllegalArgumentException("Le motif de refus est obligatoire.");
        }
        if (request.getMotif() == MotifRefus.AUTRE && (request.getCommentaire() == null || request.getCommentaire().isBlank())) {
            throw new IllegalArgumentException("Le commentaire est obligatoire pour le motif AUTRE.");
        }
        
        mission.setStatut(StatutMission.REFUSEE);
        mission.setMotifRefus(request.getMotif().getLibelle() + (request.getCommentaire() != null && !request.getCommentaire().isBlank() ? " - " + request.getCommentaire().trim() : ""));
        mission.setEstEnCarence(false);
        mission.setDateCarence(null);
        mission.setDureeCarenceHeures(0);
        
        MissionRefus refus = new MissionRefus();
        refus.setMission(mission);
        refus.setExpert(expertCourant);
        refus.setMotif(request.getMotif());
        refus.setCommentaire(request.getCommentaire());
        missionRefusRepository.save(refus);

        Mission updated = missionStateMachineService.transition(
            mission,
            StatutMission.REFUSEE,
            expertCourant,
            "Mission refusée. Motif: " + request.getMotif().getLibelle() + (request.getCommentaire() != null && !request.getCommentaire().isBlank() ? " - " + request.getCommentaire().trim() : "")
        );
        notificationCenterService.publish(
            "MISSION_REFUSEE",
            "Mission refusée",
            "La mission " + updated.getRefSinistre() + " a été refusée par " + expertCourant.getPrenom() + " " + expertCourant.getNom() + ".",
            "/missions/" + updated.getId()
        );
        return MissionDTO.fromEntity(updated);
    }

    public MissionDTO changerStatut(Long id, StatutMission statut) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        User acteur = currentUserService.requireCurrentUser();

        if (mission.getStatut() == StatutMission.NON_CLOTUREE && statut == StatutMission.HONORAIRES) {
            statut = StatutMission.HONORAIRES;
        } else if ((mission.getStatut() == StatutMission.NON_CLOTUREE || mission.getStatut() == StatutMission.ACCEPTEE) && statut != StatutMission.CLOTUREE) {
            statut = StatutMission.EN_COURS;
        }
        
        if (statut == StatutMission.CLOTUREE) {
            mission.setDateCloture(LocalDateTime.now());
        }
        if (statut == StatutMission.EN_COURS) {
            mission.setEstEnCarence(false);
            mission.setDateCarence(null);
            mission.setDureeCarenceHeures(0);
        }
        
        Mission updated = missionStateMachineService.transition(mission, statut, acteur, "Mise à jour du statut métier.");
        notificationCenterService.publish(
            "MISSION_STATUT",
            "Changement de statut mission",
            "La mission " + updated.getRefSinistre() + " est passée au statut " + updated.getStatut().getLibelle() + ".",
            "/missions/" + updated.getId()
        );
        return MissionDTO.fromEntity(updated);
    }

    // ───────────────────────── Photos ─────────────────────────
    public List<PhotoDTO> getPhotos(Long missionId) {
        missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        return photoRepository.findByMissionIdOrderByDateUploadDesc(missionId)
            .stream()
            .map(PhotoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public PhotoDTO uploadPhoto(Long missionId, MultipartFile file, String categorie, String type, String description) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        try {
            // Créer le répertoire si nécessaire
            Path uploadPath = Paths.get("uploads", "missions", missionId.toString(), "photos");
            Files.createDirectories(uploadPath);

            // Générer un nom de fichier unique
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Créer l'entité Photo
            Photo photo = new Photo();
            photo.setMission(mission);
            photo.setUrl("/uploads/missions/" + missionId + "/photos/" + fileName);
            photo.setCategorie(CategoriePhoto.valueOf(categorie));
            photo.setType(TypePhoto.valueOf(type));
            photo.setDescription(description);
            photo.setDateUpload(LocalDateTime.now());

            Photo saved = photoRepository.save(photo);
            return PhotoDTO.fromEntity(saved);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier", e);
        }
    }

    public void deletePhoto(Long missionId, Long photoId) {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("Photo non trouvée"));

        if (!photo.getMission().getId().equals(missionId)) {
            throw new RuntimeException("Photo n'appartient pas à cette mission");
        }

        // Supprimer le fichier physique
        try {
            Path filePath = Paths.get("uploads", "missions", missionId.toString(), "photos",
                photo.getUrl().substring(photo.getUrl().lastIndexOf("/") + 1));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log l'erreur mais continue la suppression en base
        }

        photoRepository.delete(photo);
    }

    // ───────────────────────── Devis ─────────────────────────
    public List<DevisDTO> getDevis(Long missionId) {
        missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        return devisRepository.findByMissionIdOrderByDateCreationDesc(missionId)
            .stream()
            .map(DevisDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public DevisDTO createDevis(Long missionId, DevisDTO devisDTO) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        Devis devis = new Devis();
        devis.setMission(mission);
        devis.setGarage(devisDTO.getGarage());
        devis.setTypeDevis(devisDTO.getTypeDevis());
        devis.setMontantPieces(devisDTO.getMontantPieces());
        devis.setMontantPeinture(devisDTO.getMontantPeinture());
        devis.setMontantMainOeuvre(devisDTO.getMontantMainOeuvre());
        devis.setTypeOperation(devisDTO.getTypeOperation());
        devis.setMontantTotal(devisDTO.getMontantTotal());
        devis.setDemandeExpertiseContradictoire(devisDTO.getDemandeExpertiseContradictoire());
        devis.setObservations(devisDTO.getObservations());
        devis.setDateCreation(LocalDateTime.now());
        devis.setMontantPiecesAccorde(devisDTO.getMontantPiecesAccorde());
        devis.setMontantPeintureAccorde(devisDTO.getMontantPeintureAccorde());
        devis.setMontantMainOeuvreAccorde(devisDTO.getMontantMainOeuvreAccorde());

        Devis saved = devisRepository.save(devis);
        return DevisDTO.fromEntity(saved);
    }

    public DevisDTO updateDevis(Long missionId, Long devisId, DevisDTO devisDTO) {
        Devis devis = devisRepository.findById(devisId)
            .orElseThrow(() -> new RuntimeException("Devis non trouvé"));

        if (!devis.getMission().getId().equals(missionId)) {
            throw new RuntimeException("Devis n'appartient pas à cette mission");
        }

        devis.setGarage(devisDTO.getGarage());
        devis.setTypeDevis(devisDTO.getTypeDevis());
        devis.setMontantPieces(devisDTO.getMontantPieces());
        devis.setMontantPeinture(devisDTO.getMontantPeinture());
        devis.setMontantMainOeuvre(devisDTO.getMontantMainOeuvre());
        devis.setTypeOperation(devisDTO.getTypeOperation());
        devis.setMontantTotal(devisDTO.getMontantTotal());
        devis.setDemandeExpertiseContradictoire(devisDTO.getDemandeExpertiseContradictoire());
        devis.setObservations(devisDTO.getObservations());
        devis.setMontantPiecesAccorde(devisDTO.getMontantPiecesAccorde());
        devis.setMontantPeintureAccorde(devisDTO.getMontantPeintureAccorde());
        devis.setMontantMainOeuvreAccorde(devisDTO.getMontantMainOeuvreAccorde());

        Devis saved = devisRepository.save(devis);
        return DevisDTO.fromEntity(saved);
    }

    public void deleteDevis(Long missionId, Long devisId) {
        Devis devis = devisRepository.findById(devisId)
            .orElseThrow(() -> new RuntimeException("Devis non trouvé"));

        if (!devis.getMission().getId().equals(missionId)) {
            throw new RuntimeException("Devis n'appartient pas à cette mission");
        }

        devisRepository.delete(devis);
    }

    // ───────────────────────── Expertises ─────────────────────────
    public List<ExpertiseDTO> getExpertises(Long missionId) {
        missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        return expertiseRepository.findByMissionIdOrderByDateExpertiseDesc(missionId)
            .stream()
            .map(ExpertiseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public ExpertiseDTO createExpertise(Long missionId, ExpertiseDTO expertiseDTO) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        Expertise expertise = new Expertise();
        expertise.setMission(mission);
        expertise.setTypeExpertise(expertiseDTO.getTypeExpertise());
        if (expertiseDTO.getDateExpertise() != null) {
            expertise.setDateExpertise(expertiseDTO.getDateExpertise());
        }
        expertise.setLieu(expertiseDTO.getLieu());
        expertise.setKilometrage(expertiseDTO.getKilometrage());
        expertise.setEtatVehicule(expertiseDTO.getEtatVehicule());
        expertise.setEstimationDommages(expertiseDTO.getEstimationDommages());
        expertise.setMontantEstimation(expertiseDTO.getMontantEstimation());
        expertise.setCalculVVADE(expertiseDTO.getCalculVVADE());
        expertise.setArbitrage(expertiseDTO.getArbitrage());
        expertise.setExpertiseContradictoire(expertiseDTO.getExpertiseContradictoire());
        if (expertiseDTO.getDateExpertiseContradictoire() != null) {
            expertise.setDateExpertiseContradictoire(expertiseDTO.getDateExpertiseContradictoire());
        }
        expertise.setMontantExpertiseContradictoire(expertiseDTO.getMontantExpertiseContradictoire());
        expertise.setObservations(expertiseDTO.getObservations());
        expertise.setDateCreation(LocalDateTime.now());

        Expertise saved = expertiseRepository.save(expertise);
        return ExpertiseDTO.fromEntity(saved);
    }

    public ExpertiseDTO updateExpertise(Long missionId, Long expertiseId, ExpertiseDTO expertiseDTO) {
        Expertise expertise = expertiseRepository.findById(expertiseId)
            .orElseThrow(() -> new RuntimeException("Expertise non trouvée"));

        if (!expertise.getMission().getId().equals(missionId)) {
            throw new RuntimeException("Expertise n'appartient pas à cette mission");
        }

        expertise.setTypeExpertise(expertiseDTO.getTypeExpertise());
        if (expertiseDTO.getDateExpertise() != null) {
            expertise.setDateExpertise(expertiseDTO.getDateExpertise());
        }
        expertise.setLieu(expertiseDTO.getLieu());
        expertise.setKilometrage(expertiseDTO.getKilometrage());
        expertise.setEtatVehicule(expertiseDTO.getEtatVehicule());
        expertise.setEstimationDommages(expertiseDTO.getEstimationDommages());
        expertise.setMontantEstimation(expertiseDTO.getMontantEstimation());
        expertise.setCalculVVADE(expertiseDTO.getCalculVVADE());
        expertise.setArbitrage(expertiseDTO.getArbitrage());
        expertise.setExpertiseContradictoire(expertiseDTO.getExpertiseContradictoire());
        if (expertiseDTO.getDateExpertiseContradictoire() != null) {
            expertise.setDateExpertiseContradictoire(expertiseDTO.getDateExpertiseContradictoire());
        }
        expertise.setMontantExpertiseContradictoire(expertiseDTO.getMontantExpertiseContradictoire());
        expertise.setObservations(expertiseDTO.getObservations());

        Expertise saved = expertiseRepository.save(expertise);
        return ExpertiseDTO.fromEntity(saved);
    }

    public void deleteExpertise(Long missionId, Long expertiseId) {
        Expertise expertise = expertiseRepository.findById(expertiseId)
            .orElseThrow(() -> new RuntimeException("Expertise non trouvée"));

        if (!expertise.getMission().getId().equals(missionId)) {
            throw new RuntimeException("Expertise n'appartient pas à cette mission");
        }

        expertiseRepository.delete(expertise);
    }

    // ───────────────────────── Notes Honoraires ─────────────────────────
    public List<NoteHonoraireDTO> getNotesHonoraire(Long missionId) {
        missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        return noteHonoraireRepository.findByMissionIdOrderByDateCreationDesc(missionId)
            .stream()
            .map(NoteHonoraireDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public NoteHonoraireDTO createNoteHonoraire(Long missionId, NoteHonoraireDTO noteHonoraireDTO) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        NoteHonoraire noteHonoraire = new NoteHonoraire();
        noteHonoraire.setMission(mission);
        noteHonoraire.setMontant(noteHonoraireDTO.getMontant());
        noteHonoraire.setUrlFichier(noteHonoraireDTO.getUrlFichier());
        noteHonoraire.setDateCreation(LocalDateTime.now());
        noteHonoraire.setStatut(noteHonoraireDTO.getStatut());

        NoteHonoraire saved = noteHonoraireRepository.save(noteHonoraire);
        return NoteHonoraireDTO.fromEntity(saved);
    }

    public NoteHonoraireDTO updateNoteHonoraire(Long missionId, Long noteId, NoteHonoraireDTO noteHonoraireDTO) {
        NoteHonoraire noteHonoraire = noteHonoraireRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("Note honoraire non trouvée"));

        if (!noteHonoraire.getMission().getId().equals(missionId)) {
            throw new RuntimeException("Note honoraire n'appartient pas à cette mission");
        }

        noteHonoraire.setMontant(noteHonoraireDTO.getMontant());
        noteHonoraire.setUrlFichier(noteHonoraireDTO.getUrlFichier());
        noteHonoraire.setStatut(noteHonoraireDTO.getStatut());

        NoteHonoraire saved = noteHonoraireRepository.save(noteHonoraire);
        return NoteHonoraireDTO.fromEntity(saved);
    }

    public void deleteNoteHonoraire(Long missionId, Long noteId) {
        NoteHonoraire noteHonoraire = noteHonoraireRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("Note honoraire non trouvée"));

        if (!noteHonoraire.getMission().getId().equals(missionId)) {
            throw new RuntimeException("Note honoraire n'appartient pas à cette mission");
        }

        noteHonoraireRepository.delete(noteHonoraire);
    }

    // ───────────────────────── Actions ─────────────────────────
    public MissionDTO cloturer(Long id) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        User acteur = currentUserService.requireCurrentUser();

        if (mission.getStatut() == StatutMission.CLOTUREE) {
            throw new RuntimeException("Mission déjà clôturée");
        }

        mission.setDateCloture(LocalDateTime.now());
        mission.setEstEnCarence(false);

        Mission updated = missionStateMachineService.transition(mission, StatutMission.CLOTUREE, acteur, "Mission clôturée.");
        notificationCenterService.publish(
            "MISSION_CLOTUREE",
            "Mission clôturée",
            "La mission " + updated.getRefSinistre() + " a été clôturée.",
            "/missions/" + updated.getId()
        );
        return MissionDTO.fromEntity(updated);
    }

    public MissionDTO rouvrir(Long id, MissionReouvertureRequest request) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        User acteur = currentUserService.requireCurrentUser();

        if (mission.getStatut() != StatutMission.CLOTUREE) {
            throw new RuntimeException("Seules les missions clôturées peuvent être rouvertes");
        }
        if (request.getMotif() == null || request.getMotif().isBlank()) {
            throw new IllegalArgumentException("Le motif de réouverture est obligatoire.");
        }

        mission.setDateCloture(null);
        mission.setDateReouverture(LocalDateTime.now());
        mission.setEstEnCarence(false);

        MissionReouverture reouverture = new MissionReouverture();
        reouverture.setMission(mission);
        reouverture.setMotif(request.getMotif());
        reouverture.setCommentaire(request.getCommentaire());
        reouverture.setOuvertPar(acteur);
        missionReouvertureRepository.save(reouverture);

        Mission updated = missionStateMachineService.transition(mission, StatutMission.REEXAMEN, acteur, "Mission rouverte. Motif: " + request.getMotif() + (request.getCommentaire() != null && !request.getCommentaire().isBlank() ? " - " + request.getCommentaire() : ""));
        notificationCenterService.publish(
            "MISSION_REEXAMEN",
            "Mission rouverte",
            "La mission " + updated.getRefSinistre() + " a été rouverte pour réexamen.",
            "/missions/" + updated.getId()
        );
        return MissionDTO.fromEntity(updated);
    }

    public MissionDTO signalerInvestigation(Long id) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        mission.setObservations((mission.getObservations() != null ? mission.getObservations() + "\n" : "")
            + "[Investigation signalée " + LocalDateTime.now() + "]");

        Mission updated = missionRepository.save(mission);
        return MissionDTO.fromEntity(updated);
    }

    public List<MissionDTO> getMissionsEnCarencePourExpertConnecte() {
        User expert = currentUserService.requireCurrentUser();
        return missionRepository.findByExpertAndEstEnCarenceTrueOrderByDateCarenceDesc(expert)
            .stream()
            .map(MissionDTO::fromEntity)
            .toList();
    }

    public MissionDTO sortirDeCarence(Long id) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        User acteur = currentUserService.requireCurrentUser();

        mission.setEstEnCarence(false);
        mission.setDateCarence(null);
        mission.setDureeCarenceHeures(0);
        Mission updated = missionStateMachineService.transition(mission, StatutMission.NON_CLOTUREE, acteur, "Sortie manuelle de carence par l'expert.");
        notificationCenterService.publish(
            "MISSION_CARENCE_TRAITEE",
            "Mission sortie de carence",
            "La mission " + updated.getRefSinistre() + " n'est plus en carence.",
            "/missions/" + updated.getId()
        );
        return MissionDTO.fromEntity(updated);
    }

    public List<MissionRefusDTO> listRefuseesPourAdmin() {
        return missionRefusRepository.findAllByOrderByDateRefusDesc()
            .stream()
            .map(MissionRefusDTO::fromEntity)
            .toList();
    }

    public MissionDTO reaffecter(Long missionId, Long expertId) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        User admin = currentUserService.requireCurrentUser();
        User nouvelExpert = userRepository.findById(expertId)
            .orElseThrow(() -> new RuntimeException("Expert introuvable"));

        mission.setExpert(nouvelExpert);
        mission.setMotifRefus(null);
        mission.setDateAffectation(LocalDateTime.now());
        mission.setEstEnCarence(false);
        mission.setDateCarence(null);
        mission.setDureeCarenceHeures(0);

        Mission updated = missionStateMachineService.transition(mission, StatutMission.NOUVELLE, admin, "Mission réaffectée à " + nouvelExpert.getPrenom() + " " + nouvelExpert.getNom() + ".");
        notificationCenterService.publish(
            "CHANGEMENT_EXPERT",
            "Mission réaffectée",
            "La mission " + updated.getRefSinistre() + " a été réaffectée à " + nouvelExpert.getPrenom() + " " + nouvelExpert.getNom() + ".",
            "/missions/" + updated.getId()
        );
        return MissionDTO.fromEntity(updated);
    }

    public List<MissionTransitionDTO> getTransitions(Long missionId) {
        return missionTransitionRepository.findByMissionIdOrderByDateTransitionAsc(missionId)
            .stream()
            .map(MissionTransitionDTO::fromEntity)
            .toList();
    }

    public int appliquerCarence(Long missionId, int seuilHeures) {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        if (mission.getDateAffectation() == null || mission.getDateCloture() != null) {
            return 0;
        }

        long duree = java.time.Duration.between(mission.getDateAffectation(), LocalDateTime.now()).toHours();
        mission.setDureeCarenceHeures((int) Math.max(0, duree));

        if (duree < seuilHeures || Boolean.TRUE.equals(mission.getEstEnCarence())) {
            missionRepository.save(mission);
            return 0;
        }

        mission.setEstEnCarence(true);
        mission.setDateCarence(LocalDateTime.now());
        missionStateMachineService.transition(mission, StatutMission.CARENCE, null, "Passage automatique en carence après " + seuilHeures + "h.");

        notificationCenterService.publish(
            "MISSION_EN_CARENCE",
            "Mission en carence",
            "Mission N°" + mission.getNumeroMission() + " est en carence.",
            "/missions/" + mission.getId()
        );
        return 1;
    }

    // ───────────────────────── Statistiques ─────────────────────────
    public Map<String, Object> getStatsMensuelles(LocalDate dateDebut, LocalDate dateFin) {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime debut = dateDebut.atStartOfDay();
        LocalDateTime fin = dateFin.atTime(LocalTime.MAX);

        List<Mission> missions = missionRepository.findByDateCreationBetween(debut, fin);

        stats.put("totalMissions", missions.size());
        stats.put("missionsCloturees", missions.stream()
            .filter(m -> m.getStatut() == StatutMission.CLOTUREE).count());
        stats.put("missionsNonCloturees", missions.stream()
            .filter(m -> m.getStatut() == StatutMission.NON_CLOTUREE).count());
        stats.put("missionsRefusees", missions.stream()
            .filter(m -> m.getStatut() == StatutMission.REFUSEE).count());

        return stats;
    }

    public Map<String, Object> getTauxCloture() {
        Map<String, Object> stats = new HashMap<>();

        long total = missionRepository.count();
        long cloturees = missionRepository.countByStatut(StatutMission.CLOTUREE);

        double taux = total > 0 ? (double) cloturees / total * 100 : 0;

        stats.put("totalMissions", total);
        stats.put("missionsCloturees", cloturees);
        stats.put("tauxCloture", Math.round(taux * 100.0) / 100.0);

        return stats;
    }

    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Mission> missions = missionRepository.findAll();

        // Délai moyen de traitement (en jours)
        double delaiMoyen = missions.stream()
            .filter(m -> m.getDateAffectation() != null && m.getDateCloture() != null)
            .mapToLong(m -> java.time.Duration.between(m.getDateAffectation(), m.getDateCloture()).toDays())
            .average()
            .orElse(0.0);

        stats.put("delaiMoyenTraitement", Math.round(delaiMoyen * 100.0) / 100.0);

        // Taux de missions en carence (> 48h sans affectation)
        long enCarence = missionRepository.findMissionsEnCarence(
            LocalDateTime.now().minusHours(48)).size();
        stats.put("missionsEnCarence", enCarence);

        return stats;
    }
}
