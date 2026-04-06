package com.wafa.assurance.model;

public enum TypeOperation {
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
