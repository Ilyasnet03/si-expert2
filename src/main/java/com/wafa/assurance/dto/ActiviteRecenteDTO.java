package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO pour les activités récentes affichées dans la timeline du dashboard.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiviteRecenteDTO {
    private Long id;
    private String type;       // MISSION_CREEE, EXPERTISE_AJOUTEE, DEVIS_GENERE, PHOTO_UPLOADEE, MISSION_CLOTUREE
    private String message;
    private String dateRelative; // "il y a 5 minutes", "il y a 2 heures"
    private String date;        // Date formatée
    private String lien;        // URL vers la ressource
    private String icone;       // classe CSS de l'icône
    private String couleur;     // classe CSS de couleur
}
