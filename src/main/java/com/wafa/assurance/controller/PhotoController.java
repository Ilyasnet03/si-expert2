package com.wafa.assurance.controller;

import com.wafa.assurance.dto.PhotoDTO;
import com.wafa.assurance.model.CategoriePhoto;
import com.wafa.assurance.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/missions/{missionId}/photos")
@CrossOrigin(origins = "http://localhost:3000")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping
    public ResponseEntity<List<PhotoDTO>> list(@PathVariable Long missionId) {
        return ResponseEntity.ok(photoService.findByMission(missionId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDTO> upload(
            @PathVariable Long missionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam CategoriePhoto categorie,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String type) throws IOException {
        return ResponseEntity.status(201).body(
            photoService.sauvegarder(missionId, file, categorie, description, type)
        );
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PhotoDTO>> uploadBulk(
            @PathVariable Long missionId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam CategoriePhoto categorie,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) List<String> descriptions) throws IOException {
        return ResponseEntity.status(201).body(photoService.sauvegarderMultiple(missionId, files, categorie, type, descriptions));
    }

    @GetMapping("/{photoId}/image")
    public ResponseEntity<Resource> download(@PathVariable Long photoId) throws IOException {
        Resource resource = photoService.telecharger(photoId);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable Long missionId, @PathVariable Long photoId) throws IOException {
        photoService.supprimer(photoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadZip(@PathVariable Long missionId) throws IOException {
        Resource resource = photoService.zipByMission(missionId);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"photos-mission-" + missionId + ".zip\"")
            .body(resource);
    }
}
