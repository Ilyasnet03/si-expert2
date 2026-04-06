package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expertises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expertise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // Informations générales
    @Column(name = "date_expertise")
    private LocalDate dateExpertise;

    @Column(name = "lieu", length = 500)
    private String lieu;

    @Column(name = "kilometrage")
    private Integer kilometrage;

    @Column(name = "etat_vehicule", columnDefinition = "TEXT")
    private String etatVehicule;

    // Estimation et calcul
    @Column(name = "estimation_dommages", columnDefinition = "TEXT")
    private String estimationDommages;

    @Column(name = "montant_estimation", precision = 10, scale = 2)
    private BigDecimal montantEstimation;

    @Column(name = "calcul_vvade")
    private String calculVVADE;

    @Column(name = "arbitrage", columnDefinition = "TEXT")
    private String arbitrage;

    // Expertise contradictoire
    @Column(name = "expertise_contradictoire", columnDefinition = "TEXT")
    private String expertiseContradictoire;

    @Column(name = "date_expertise_contradictoire")
    private LocalDate dateExpertiseContradictoire;

    @Column(name = "montant_expertise_contradictoire", precision = 10, scale = 2)
    private BigDecimal montantExpertiseContradictoire;

    // Observations supplémentaires
    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
