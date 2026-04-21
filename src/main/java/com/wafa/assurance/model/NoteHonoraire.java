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
@Table(name = "notes_honoraires")
public class NoteHonoraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "numero_note")
    private String numeroNote;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "montant", precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "montant_ht", precision = 10, scale = 2)
    private BigDecimal montantHT;

    @Column(name = "taux_tva", precision = 10, scale = 2)
    private BigDecimal tauxTVA;

    @Column(name = "montant_tva", precision = 10, scale = 2)
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc", precision = 10, scale = 2)
    private BigDecimal montantTTC;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @Column(name = "nom_fichier")
    private String nomFichier;

    @Column(name = "url_fichier")
    private String urlFichier;

    @Column(name = "date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutNoteHonoraire statut;

    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutNoteHonoraire.EMISE;
        }
    }
}
