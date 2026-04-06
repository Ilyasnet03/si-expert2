package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes_honoraires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteHonoraire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // Détails de la note
    @Column(name = "numero_note", unique = true, nullable = false)
    private String numeroNote;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "montant_ht", precision = 10, scale = 2, nullable = false)
    private BigDecimal montantHT;

    @Column(name = "taux_tva", precision = 5, scale = 2)
    private BigDecimal tauxTVA;

    @Column(name = "montant_tva", precision = 10, scale = 2)
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc", precision = 10, scale = 2, nullable = false)
    private BigDecimal montantTTC;

    // Fichier PDF
    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(name = "statut_paiement", length = 50)
    private String statutPaiement;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.tauxTVA == null) {
            this.tauxTVA = new BigDecimal("20.00");
        }
        if (this.montantTVA == null) {
            this.montantTVA = this.montantHT.multiply(this.tauxTVA).divide(new BigDecimal("100"));
        }
        if (this.montantTTC == null) {
            this.montantTTC = this.montantHT.add(this.montantTVA);
        }
        if (this.statutPaiement == null) {
            this.statutPaiement = "EN_ATTENTE";
        }
    }
}
