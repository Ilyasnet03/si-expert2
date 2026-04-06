package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriePhoto categorie;

    @Column(nullable = false)
    private String cheminFichier;

    @Column(nullable = false)
    private String nomOriginal;

    @Column(nullable = false)
    private Long tailleFichier;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
