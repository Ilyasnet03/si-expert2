package com.wafa.assurance.controller;

import com.wafa.assurance.dto.ExpertiseDTO;
import com.wafa.assurance.dto.ExpertiseRequest;
import com.wafa.assurance.dto.VvadeRequest;
import com.wafa.assurance.dto.VvadeResponse;
import com.wafa.assurance.service.ExpertiseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/missions/{missionId}/expertise")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpertiseController {

    @Autowired
    private ExpertiseService expertiseService;

    @GetMapping
    public ResponseEntity<ExpertiseDTO> get(@PathVariable Long missionId) {
        return ResponseEntity.ok(expertiseService.getOrCreateByMission(missionId));
    }

    @PutMapping
    public ResponseEntity<ExpertiseDTO> update(
            @PathVariable Long missionId,
            @RequestBody @Valid ExpertiseRequest req) {
        return ResponseEntity.ok(expertiseService.update(missionId, req));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpertiseDTO> updateMultipart(
            @PathVariable Long missionId,
            @RequestPart("data") @Valid ExpertiseRequest req,
            @RequestPart(value = "carnetEntretien", required = false) MultipartFile carnetEntretien,
            @RequestPart(value = "rapportDefinitif", required = false) MultipartFile rapportDefinitif) throws Exception {
        return ResponseEntity.ok(expertiseService.update(missionId, req, carnetEntretien, rapportDefinitif));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long missionId) {
        expertiseService.delete(missionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calculer-vvade")
    public ResponseEntity<VvadeResponse> calculerVvade(
            @PathVariable Long missionId,
            @RequestBody VvadeRequest request) {
        return ResponseEntity.ok(expertiseService.calculerVvade(missionId, request));
    }
}
