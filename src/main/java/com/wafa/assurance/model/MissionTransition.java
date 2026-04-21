package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_transitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(name = "ancien_statut")
    private StatutMission ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouveau_statut", nullable = false)
    private StatutMission nouveauStatut;

    @Column(name = "acteur_id")
    private Long acteurId;

    @Column(name = "acteur_nom")
    private String acteurNom;

    @Column(name = "acteur_role")
    private String acteurRole;

    @Column(name = "date_transition", nullable = false)
    private LocalDateTime dateTransition;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @PrePersist
    void onCreate() {
        if (dateTransition == null) {
            dateTransition = LocalDateTime.now();
        }
    }
}