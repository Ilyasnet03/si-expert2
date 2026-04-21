package com.wafa.assurance.controller;

import com.wafa.assurance.dto.NoteHonoraireDTO;
import com.wafa.assurance.service.NoteHonoraireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/honoraires")
@CrossOrigin(origins = "http://localhost:3000")
public class HonorairesGlobalController {

    @Autowired
    private NoteHonoraireService noteHonoraireService;

    @GetMapping
    public ResponseEntity<List<NoteHonoraireDTO>> listAll() {
        return ResponseEntity.ok(noteHonoraireService.findAll());
    }
}
