package com.wafa.assurance.model;

public enum TypeOperation {
    PIECES_ORIGINE("Pièces d'origine"),
    PIECES_ADAPTABLES("Pièces adaptables"),
    PIECES_OCCASION("Pièces d'occasion"),
    REPARATION("Réparation"),
    REMPLACEMENT("Remplacement"),
    REFORME("Réforme");

    private final String libelle;

    TypeOperation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
