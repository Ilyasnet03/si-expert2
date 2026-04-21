package com.wafa.assurance.model;

public enum TypePhoto {
    AVANT_REPARATION("Avant réparation"),
    COURS_REPARATION("Cours réparation"),
    APRES_REPARATION("Après réparation"),
    AUTRE("Autres");

    private final String libelle;

    TypePhoto(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}