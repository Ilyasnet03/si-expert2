package com.wafa.assurance.model;

public enum StatutNoteHonoraire {
    EMISE("Émise"),
    REGLEE("Réglée");

    private final String libelle;

    StatutNoteHonoraire(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}