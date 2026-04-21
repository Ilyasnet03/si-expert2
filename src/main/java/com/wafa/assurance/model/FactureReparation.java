package com.wafa.assurance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures_reparation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureReparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devis_id")
    private Devis devis;

    @Column(name = "numero_facture")
    private String numeroFacture;

    @Column(name = "date_facture")
    private LocalDate dateFacture;

    @Column(name = "montant_ht", precision = 10, scale = 2)
    private BigDecimal montantHT;

    @Column(name = "montant_tva", precision = 10, scale = 2)
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc", precision = 10, scale = 2)
    private BigDecimal montantTTC;

    @Column(name = "garage_emetteur")
    private String garageEmetteur;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement")
    private StatutPaiementFacture statutPaiement;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @Column(name = "nom_fichier")
    private String nomFichier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (statutPaiement == null) {
            statutPaiement = StatutPaiementFacture.EN_ATTENTE;
        }
    }
}