package com.wafa.assurance.model;

public enum CategoriePhoto {
    DOCUMENT_BASE("Document de base"),
    CONSTAT("Constat"),
    VUE_GENERALE("Vue générale"),
    DOMMAGES_AVANT("Dommages avant"),
    DOMMAGES_ARRIERE("Dommages arrière"),
    DOMMAGES_LATERAUX("Dommages latéraux"),
    DOMMAGES_INTERIEUR("Dommages intérieur"),
    SOUS_CAPOT("Sous capot"),
    PLAQUE("Plaque"),
    VIN("VIN"),
    COMPTEUR("Compteur");

    private final String libelle;

    CategoriePhoto(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
