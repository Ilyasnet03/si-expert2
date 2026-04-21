package com.wafa.assurance.controller;

import com.wafa.assurance.dto.ReferenceOptionDTO;
import com.wafa.assurance.repository.DevisRepository;
import com.wafa.assurance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reference-data")
@CrossOrigin(origins = "${app.frontend-url:http://localhost:3000}")
@RequiredArgsConstructor
public class ReferenceDataController {

    private final UserRepository userRepository;
    private final DevisRepository devisRepository;

    @GetMapping("/experts")
    public ResponseEntity<List<ReferenceOptionDTO>> experts() {
        return ResponseEntity.ok(
            userRepository.findByRoleIgnoreCaseAndActifTrue("EXPERT")
                .stream()
                .map(user -> new ReferenceOptionDTO(user.getEmail(), user.getPrenom() + " " + user.getNom()))
                .toList()
        );
    }

    @GetMapping("/garages")
    public ResponseEntity<List<ReferenceOptionDTO>> garages() {
        return ResponseEntity.ok(
            devisRepository.findDistinctGarages()
                .stream()
                .map(garage -> new ReferenceOptionDTO(garage, garage))
                .toList()
        );
    }
}