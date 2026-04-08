package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expertises")
public class Expertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeExpertise type;

    @Column(name = "date_expertise")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateExpertise;

    @Column(name = "ville")
    private String ville;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "kilometrage")
    private Integer kilometrage;

    @Column(name = "etat_vehicule", columnDefinition = "TEXT")
    private String etatVehicule;

    @Column(name = "estimation_dommages", columnDefinition = "TEXT")
    private String estimationDommages;

    @Column(name = "entretien", columnDefinition = "TEXT")
    private String entretien;

    @Column(name = "carnet_entretien", columnDefinition = "TEXT")
    private String carnetEntretien;

    @Column(name = "vvade", columnDefinition = "TEXT")
    private String vvade;

    @Column(name = "reforme", columnDefinition = "TEXT")
    private String reforme;

    @Column(name = "date_demande_contre_expertise")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateDemandeContreExpertise;

    @Column(name = "expert_adverse_designe")
    private String expertAdverseDesigne;

    @Column(name = "date_designation_expert_adverse")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateDesignationExpertAdverse;

    @Column(name = "date_expertise_adverse")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateExpertiseAdverse;

    @Column(name = "accord_experts")
    private String accordExperts;

    @Column(name = "date_demande_arbitrage")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateDemandeArbitrage;

    @Column(name = "date_designation_arbitre")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateDesignationArbitre;

    @Column(name = "expert_arbitre_designe")
    private String expertArbitreDesigne;

    @Column(name = "date_expertise_arbitrale")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateExpertiseArbitrale;

    @Column(name = "rapport_definitif", columnDefinition = "TEXT")
    private String rapportDefinitif;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @PrePersist
    protected void onCreate() {
        if (this.dateExpertise == null) {
            this.dateExpertise = LocalDateTime.now();
        }
    }
}
