package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_sinistre", unique = true, nullable = false)
    private String refSinistre;

    @Column(name = "num_police")
    private String numPolice;

    @Column(name = "immatriculation")
    private String immatriculation;

    @Column(name = "type_mission")
    private String typeMission;

    @Column(name = "parcours")
    private String parcours;

    @Column(name = "tel_assure")
    private String telAssure;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutMission statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id")
    private User expert;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @Column(name = "date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    @Column(name = "date_affectation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateAffectation;

    @Column(name = "est_en_carence")
    private Boolean estEnCarence = false;

    @Column(name = "date_carence")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCarence;

    @Column(name = "duree_carence_heures")
    private Integer dureeCarenceHeures;

    @Column(name = "date_cloture")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCloture;

    @Column(name = "date_reouverture")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateReouverture;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Devis> devis;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Photo> photos;

    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutMission.NOUVELLE;
        }
    }

    public String getNumeroMission() {
        return "M" + String.format("%05d", this.id != null ? this.id : 0);
    }
}