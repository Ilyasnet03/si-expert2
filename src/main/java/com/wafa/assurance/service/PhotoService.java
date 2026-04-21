package com.wafa.assurance.service;

import com.wafa.assurance.dto.PhotoDTO;
import com.wafa.assurance.model.CategoriePhoto;
import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.Photo;
import com.wafa.assurance.repository.MissionRepository;
import com.wafa.assurance.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private NotificationCenterService notificationCenterService;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public List<PhotoDTO> findByMission(Long missionId) {
        return photoRepository.findByMissionIdOrderByDateUploadDesc(missionId)
            .stream()
            .map(PhotoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public PhotoDTO sauvegarder(Long missionId, MultipartFile file, CategoriePhoto categorie) throws IOException {
        return sauvegarder(missionId, file, categorie, null, null);
    }

    public PhotoDTO sauvegarder(Long missionId, MultipartFile file, CategoriePhoto categorie, String description, String typePhoto) throws IOException {
        Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée: " + missionId));

        Path dir = Paths.get(uploadDir, "missions", missionId.toString(), "photos");
        Files.createDirectories(dir);

        String ext = getFileExtension(file.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + "." + ext;
        Path destination = dir.resolve(nomFichier);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        Photo photo = new Photo();
        photo.setMission(mission);
        photo.setCategorie(categorie);
        photo.setType(typePhoto != null ? com.wafa.assurance.model.TypePhoto.valueOf(typePhoto) : com.wafa.assurance.model.TypePhoto.AUTRE);
        photo.setCheminFichier(destination.toString());
        photo.setNomOriginal(file.getOriginalFilename());
        photo.setTailleFichier(file.getSize());
        photo.setDescription(description);

        Photo saved = photoRepository.save(photo);
        notificationCenterService.publish(
            "PHOTO_UPLOAD",
            "Nouvelle photo importée",
            "Une photo a été ajoutée à la mission " + mission.getRefSinistre(),
            "/missions/" + missionId + "/photos"
        );
        return PhotoDTO.from(saved);
    }

    public List<PhotoDTO> sauvegarderMultiple(Long missionId, MultipartFile[] files, CategoriePhoto categorie, String typePhoto, List<String> descriptions) throws IOException {
        java.util.ArrayList<PhotoDTO> uploaded = new java.util.ArrayList<>();
        for (int index = 0; index < files.length; index++) {
            String description = descriptions != null && descriptions.size() > index ? descriptions.get(index) : null;
            uploaded.add(sauvegarder(missionId, files[index], categorie, description, typePhoto));
        }
        return uploaded;
    }

    public Resource telecharger(Long photoId) throws IOException {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("Photo non trouvée: " + photoId));

        Resource resource = new FileSystemResource(photo.getCheminFichier());
        if (!resource.exists()) {
            throw new FileNotFoundException("Fichier introuvable: " + photo.getCheminFichier());
        }
        return resource;
    }

    public void supprimer(Long photoId) throws IOException {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("Photo non trouvée"));

        try {
            Files.deleteIfExists(Paths.get(photo.getCheminFichier()));
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
        }

        photoRepository.deleteById(photoId);
    }

    public Resource zipByMission(Long missionId) throws IOException {
        List<Photo> photos = photoRepository.findByMissionIdOrderByDateUploadDesc(missionId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (Photo photo : photos) {
                if (photo.getCheminFichier() == null) {
                    continue;
                }
                Path path = Paths.get(photo.getCheminFichier());
                if (!Files.exists(path)) {
                    continue;
                }
                zipOutputStream.putNextEntry(new ZipEntry(photo.getNomOriginal() != null ? photo.getNomOriginal() : path.getFileName().toString()));
                Files.copy(path, zipOutputStream);
                zipOutputStream.closeEntry();
            }
        }
        return new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") < 0) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
