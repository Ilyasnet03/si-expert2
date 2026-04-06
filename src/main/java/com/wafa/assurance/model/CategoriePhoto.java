package com.wafa.assurance.model;

public enum CategoriePhoto {
    AVANT("Avant sinistre"),
    APRES("Après sinistre"),
    DETAIL("Détail sinistre"),
    ORIGINAL("Pièce d'origine"),
    DEVIS("Devis"),
    DOCUMENT("Document");

    private final String libelle;

    CategoriePhoto(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
