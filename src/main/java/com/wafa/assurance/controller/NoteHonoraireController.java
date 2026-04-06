package com.wafa.assurance.controller;

import com.wafa.assurance.dto.NoteHonoraireDTO;
import com.wafa.assurance.dto.NoteHonoraireRequest;
import com.wafa.assurance.service.NoteHonoraireService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/missions/{missionId}/honoraires")
@CrossOrigin(origins = "http://localhost:3000")
public class NoteHonoraireController {

    @Autowired
    private NoteHonoraireService noteHonoraireService;

    @GetMapping
    public ResponseEntity<List<NoteHonoraireDTO>> list(@PathVariable Long missionId) {
        return ResponseEntity.ok(noteHonoraireService.findByMission(missionId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteHonoraireDTO> create(
            @PathVariable Long missionId,
            @RequestPart("data") @Valid NoteHonoraireRequest req,
            @RequestPart(value = "fichier", required = false) MultipartFile fichier) throws IOException {
        return ResponseEntity.status(201).body(
            noteHonoraireService.create(missionId, req, fichier)
        );
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteHonoraireDTO> update(
            @PathVariable Long missionId,
            @PathVariable Long noteId,
            @RequestBody @Valid NoteHonoraireRequest req) {
        return ResponseEntity.ok(noteHonoraireService.update(noteId, req));
    }

    @GetMapping("/{noteId}/fichier")
    public ResponseEntity<Resource> download(@PathVariable Long noteId) throws IOException {
        Resource resource = noteHonoraireService.telecharger(noteId);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long missionId,
            @PathVariable Long noteId) throws IOException {
        noteHonoraireService.delete(noteId);
        return ResponseEntity.noContent().build();
    }
}
