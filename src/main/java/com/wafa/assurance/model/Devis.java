package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "devis")
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "garage")
    private String garage;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_devis")
    private TypeDevis typeDevis;

    @Column(name = "montant_pieces", precision = 10, scale = 2)
    private BigDecimal montantPieces;

    @Column(name = "montant_peinture", precision = 10, scale = 2)
    private BigDecimal montantPeinture;

    @Column(name = "montant_main_oeuvre", precision = 10, scale = 2)
    private BigDecimal montantMainOeuvre;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_operation")
    private TypeOperation typeOperation;

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "demande_expertise_contradictoire")
    private Boolean demandeExpertiseContradictoire;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutDevis statut;

    @Column(name = "chemin_image")
    private String cheminImage;

    @Column(name = "date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    @Column(name = "montant_pieces_accorde", precision = 10, scale = 2)
    private BigDecimal montantPiecesAccorde;

    @Column(name = "montant_peinture_accorde", precision = 10, scale = 2)
    private BigDecimal montantPeintureAccorde;

    @Column(name = "montant_main_oeuvre_accorde", precision = 10, scale = 2)
    private BigDecimal montantMainOeuvreAccorde;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_operation_accorde")
    private TypeOperation typeOperationAccorde;

    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.demandeExpertiseContradictoire == null) {
            this.demandeExpertiseContradictoire = false;
        }
    }

    public BigDecimal getMontantTotalAccorde() {
        if (montantPiecesAccorde != null && montantPeintureAccorde != null && montantMainOeuvreAccorde != null) {
            return montantPiecesAccorde.add(montantPeintureAccorde).add(montantMainOeuvreAccorde);
        }
        return null;
    }
}