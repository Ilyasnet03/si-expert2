package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_refus")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionRefus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private User expert;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif", nullable = false)
    private MotifRefus motif;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_refus", nullable = false)
    private LocalDateTime dateRefus;

    @PrePersist
    void onCreate() {
        if (dateRefus == null) {
            dateRefus = LocalDateTime.now();
        }
    }
}