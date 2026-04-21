package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Column(name = "date_mise_en_circulation")
    private LocalDate dateMiseEnCirculation;

    @Column(name = "etat_general")
    private String etatGeneral;

    @Column(name = "etat_vehicule", columnDefinition = "TEXT")
    private String etatVehicule;

    @Column(name = "estimation_dommages", columnDefinition = "TEXT")
    private String estimationDommages;

    @Column(name = "montant_estimation", precision = 10, scale = 2)
    private BigDecimal montantEstimation;

    @Column(name = "entretien", columnDefinition = "TEXT")
    private String entretien;

    @Column(name = "carnet_entretien", columnDefinition = "TEXT")
    private String carnetEntretien;

    @Column(name = "carnet_entretien_present")
    private Boolean carnetEntretienPresent;

    @Column(name = "options_specifiques", columnDefinition = "TEXT")
    private String optionsSpecifiques;

    @Column(name = "sinistres_anterieurs")
    private Boolean sinistresAnterieurs;

    @Column(name = "cote_argus", precision = 12, scale = 2)
    private BigDecimal coteArgus;

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

    @Column(name = "arbitrage")
    private String arbitrage;

    @Column(name = "expertise_contradictoire")
    private String expertiseContradictoire;

    @Column(name = "date_expertise_contradictoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateExpertiseContradictoire;

    @Column(name = "montant_expertise_contradictoire", precision = 10, scale = 2)
    private BigDecimal montantExpertiseContradictoire;

    @Column(name = "date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

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

    public TypeExpertise getTypeExpertise() {
        return this.type;
    }

    public void setTypeExpertise(TypeExpertise typeExpertise) {
        this.type = typeExpertise;
    }

    public String getLieu() {
        return this.ville;
    }

    public void setLieu(String lieu) {
        this.ville = lieu;
    }

    public String getCalculVVADE() {
        return this.vvade;
    }

    public void setCalculVVADE(String calculVVADE) {
        this.vvade = calculVVADE;
    }

    public LocalDateTime getDateExpertiseContradictoire() {
        return this.dateExpertiseContradictoire;
    }

    public void setDateExpertiseContradictoire(LocalDateTime dateExpertiseContradictoire) {
        this.dateExpertiseContradictoire = dateExpertiseContradictoire;
    }

    public String getExpertiseContradictoire() {
        return this.expertiseContradictoire;
    }

    public void setExpertiseContradictoire(String expertiseContradictoire) {
        this.expertiseContradictoire = expertiseContradictoire;
    }

    public BigDecimal getMontantEstimation() {
        return this.montantEstimation;
    }

    public void setMontantEstimation(BigDecimal montantEstimation) {
        this.montantEstimation = montantEstimation;
    }

    public BigDecimal getMontantExpertiseContradictoire() {
        return this.montantExpertiseContradictoire;
    }

    public void setMontantExpertiseContradictoire(BigDecimal montantExpertiseContradictoire) {
        this.montantExpertiseContradictoire = montantExpertiseContradictoire;
    }

    public LocalDateTime getDateCreation() {
        return this.dateCreation != null ? this.dateCreation : this.dateExpertise;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
