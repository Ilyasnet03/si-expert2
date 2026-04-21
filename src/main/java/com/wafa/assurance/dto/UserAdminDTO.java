package com.wafa.assurance.dto;

import com.wafa.assurance.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String email;
    private String telephone;
    private String matriculeProfessionnel;
    private String role;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    private List<String> specialites;
    private List<String> zonesIntervention;
    private Integer maxMissionsSimultanees;
    private Double noteMinimaleRequise;
    private String temporaryPassword;
    private boolean envoyerEmailBienvenue;

    public static UserAdminDTO from(User user) {
        return UserAdminDTO.builder()
            .id(user.getId())
            .nom(user.getNom())
            .prenom(user.getPrenom())
            .nomComplet((user.getPrenom() != null ? user.getPrenom() : "") + " " + (user.getNom() != null ? user.getNom() : ""))
            .email(user.getEmail())
            .telephone(user.getTelephone())
            .matriculeProfessionnel(user.getMatriculeProfessionnel())
            .role(user.getRole())
            .statut(user.getStatutCompte())
            .dateCreation(user.getCreatedAt())
            .derniereConnexion(user.getDerniereConnexion())
            .specialites(user.getSpecialites() != null ? user.getSpecialites() : new ArrayList<>())
            .zonesIntervention(user.getZonesIntervention() != null ? user.getZonesIntervention() : new ArrayList<>())
            .maxMissionsSimultanees(user.getMaxMissionsSimultanees())
            .noteMinimaleRequise(user.getNoteMinimaleRequise())
            .build();
    }
}