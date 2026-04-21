package com.wafa.assurance.controller;

import com.wafa.assurance.dto.FactureReparationDTO;
import com.wafa.assurance.dto.FactureReparationRequest;
import com.wafa.assurance.service.FactureReparationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/missions/{missionId}/factures")
@CrossOrigin(origins = "${app.frontend-url:http://localhost:3000}")
@RequiredArgsConstructor
public class FactureReparationController {

    private final FactureReparationService factureService;

    @GetMapping
    public ResponseEntity<List<FactureReparationDTO>> list(@PathVariable Long missionId) {
        return ResponseEntity.ok(factureService.findByMission(missionId));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<FactureReparationDTO> create(
        @PathVariable Long missionId,
        @RequestPart("data") @Valid FactureReparationRequest req,
        @RequestPart(value = "fichier", required = false) MultipartFile fichier
    ) throws IOException {
        return ResponseEntity.status(201).body(factureService.create(missionId, req, fichier));
    }

    @PutMapping("/{factureId}")
    public ResponseEntity<FactureReparationDTO> update(
        @PathVariable Long missionId,
        @PathVariable Long factureId,
        @RequestBody @Valid FactureReparationRequest req
    ) {
        return ResponseEntity.ok(factureService.update(factureId, req));
    }

    @GetMapping("/{factureId}/fichier")
    public ResponseEntity<Resource> download(@PathVariable Long missionId, @PathVariable Long factureId) throws IOException {
        Resource resource = factureService.download(factureId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @DeleteMapping("/{factureId}")
    public ResponseEntity<Void> delete(@PathVariable Long missionId, @PathVariable Long factureId) throws IOException {
        factureService.delete(factureId);
        return ResponseEntity.noContent().build();
    }
}