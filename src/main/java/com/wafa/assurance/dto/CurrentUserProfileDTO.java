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
public class CurrentUserProfileDTO {
    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private String role;
    private String telephone;
    private String matriculeProfessionnel;
    private String statutCompte;
    private Boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime derniereConnexion;
    private List<String> specialites;
    private List<String> zonesIntervention;
    private Integer maxMissionsSimultanees;
    private Double noteMinimaleRequise;

    public static CurrentUserProfileDTO from(User user) {
        return CurrentUserProfileDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .prenom(user.getPrenom())
            .nom(user.getNom())
            .role(user.getRole())
            .telephone(user.getTelephone())
            .matriculeProfessionnel(user.getMatriculeProfessionnel())
            .statutCompte(user.getStatutCompte())
            .actif(user.getActif())
            .createdAt(user.getCreatedAt())
            .derniereConnexion(user.getDerniereConnexion())
            .specialites(user.getSpecialites() != null ? user.getSpecialites() : new ArrayList<>())
            .zonesIntervention(user.getZonesIntervention() != null ? user.getZonesIntervention() : new ArrayList<>())
            .maxMissionsSimultanees(user.getMaxMissionsSimultanees())
            .noteMinimaleRequise(user.getNoteMinimaleRequise())
            .build();
    }
}