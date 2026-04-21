package com.wafa.assurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "url")
    private String url;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie")
    private CategoriePhoto categorie;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypePhoto type;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @Column(name = "nom_original")
    private String nomOriginal;

    @Column(name = "taille_fichier")
    private Long tailleFichier;

    @Column(name = "date_upload")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateUpload;

    @PrePersist
    protected void onCreate() {
        if (this.dateUpload == null) {
            this.dateUpload = LocalDateTime.now();
        }
    }
}
