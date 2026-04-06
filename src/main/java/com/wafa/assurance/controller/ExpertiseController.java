package com.wafa.assurance.controller;

import com.wafa.assurance.dto.ExpertiseDTO;
import com.wafa.assurance.dto.ExpertiseRequest;
import com.wafa.assurance.service.ExpertiseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long missionId) {
        expertiseService.delete(missionId);
        return ResponseEntity.noContent().build();
    }
}
