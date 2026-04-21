package com.wafa.assurance.model;

public enum StatutPaiementFacture {
    EN_ATTENTE("En attente"),
    REGLEE("Réglée"),
    PARTIELLE("Partiellement réglée");

    private final String libelle;

    StatutPaiementFacture(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}