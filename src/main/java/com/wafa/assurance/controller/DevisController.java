package com.wafa.assurance.controller;

import com.wafa.assurance.dto.AccordDevisRequest;
import com.wafa.assurance.dto.DevisDTO;
import com.wafa.assurance.dto.DevisRequest;
import com.wafa.assurance.service.DevisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/missions/{missionId}/devis")
@CrossOrigin(origins = "http://localhost:3000")
public class DevisController {

    @Autowired
    private DevisService devisService;

    @GetMapping
    public ResponseEntity<List<DevisDTO>> list(@PathVariable Long missionId) {
        return ResponseEntity.ok(devisService.findByMission(missionId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DevisDTO> create(
            @PathVariable Long missionId,
            @RequestPart("data") @Valid DevisRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.status(201).body(devisService.create(missionId, req, image));
    }

    @PatchMapping("/{devisId}/accorder")
    public ResponseEntity<DevisDTO> accorder(
            @PathVariable Long missionId,
            @PathVariable Long devisId,
            @RequestBody @Valid AccordDevisRequest req) {
        return ResponseEntity.ok(devisService.accorder(devisId, req));
    }

    @PutMapping("/{devisId}")
    public ResponseEntity<DevisDTO> update(
        @PathVariable Long missionId,
        @PathVariable Long devisId,
        @RequestBody @Valid DevisRequest req) {
        return ResponseEntity.ok(devisService.update(devisId, req));
    }

    @GetMapping("/{devisId}/image")
    public ResponseEntity<Resource> image(@PathVariable Long missionId, @PathVariable Long devisId) throws java.io.IOException {
        java.nio.file.Path imagePath = devisService.getImagePath(devisId);
        Resource resource = new FileSystemResource(imagePath);
        String contentType = java.nio.file.Files.probeContentType(imagePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @DeleteMapping("/{devisId}")
    public ResponseEntity<Void> delete(@PathVariable Long missionId, @PathVariable Long devisId) throws IOException {
        devisService.delete(devisId);
        return ResponseEntity.noContent().build();
    }
}
