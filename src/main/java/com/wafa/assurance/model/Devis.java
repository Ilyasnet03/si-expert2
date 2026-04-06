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
@Table(name = "devis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Devis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(nullable = false)
    private String garage;

    @Enumerated(EnumType.STRING)
    private TypeDevis typeDevis;

    private BigDecimal montantPieces;
    private BigDecimal montantPeinture;
    private BigDecimal montantMainOeuvre;
    private BigDecimal montantTotal;

    // Montants accordés par l'expert
    private BigDecimal montantAccordePieces;
    private BigDecimal montantAccordePeinture;
    private BigDecimal montantAccordeMainOeuvre;
    private BigDecimal montantAccordeTotal;

    @Enumerated(EnumType.STRING)
    private TypeOperation typeOperation;

    private boolean expertiseContradictoire;

    @Column(length = 1000)
    private String observations;

    @Enumerated(EnumType.STRING)
    private StatutDevis statut;

    // Chemin vers le fichier image (stockage disque)
    private String cheminImage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}