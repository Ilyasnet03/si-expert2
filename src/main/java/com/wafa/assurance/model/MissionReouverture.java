package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_reouvertures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionReouverture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "motif", nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ouvert_par_id", nullable = false)
    private User ouvertPar;

    @Column(name = "date_reouverture", nullable = false)
    private LocalDateTime dateReouverture;

    @PrePersist
    void onCreate() {
        if (dateReouverture == null) {
            dateReouverture = LocalDateTime.now();
        }
    }
}