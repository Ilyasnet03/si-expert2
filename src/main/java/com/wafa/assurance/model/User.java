package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "keycloak_user_id")
    private String keycloakUserId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "matricule_professionnel")
    private String matriculeProfessionnel;

    @Column(name = "role")
    private String role = "EXPERT";

    @Column(name = "actif")
    private Boolean actif = true;

    @Column(name = "statut_compte")
    private String statutCompte = "ACTIF";

    @Column(name = "derniere_connexion")
    private java.time.LocalDateTime derniereConnexion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_specialites", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "specialite")
    private java.util.List<String> specialites = new java.util.ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_zones", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "zone")
    private java.util.List<String> zonesIntervention = new java.util.ArrayList<>();

    @Column(name = "max_missions_simultanees")
    private Integer maxMissionsSimultanees;

    @Column(name = "note_minimale_requise")
    private Double noteMinimaleRequise;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (statutCompte == null) {
            statutCompte = Boolean.TRUE.equals(actif) ? "ACTIF" : "INACTIF";
        }
    }
}
