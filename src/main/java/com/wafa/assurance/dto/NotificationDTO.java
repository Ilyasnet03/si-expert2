package com.wafa.assurance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO pour les notifications du dashboard admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean lu;
    private String date;
    private String type;    // NOUVEAU_SINISTRE, MISSION_RETARD, DEVIS_A_VALIDER
    private String lien;
    private String icone;
    private String couleur;
}
