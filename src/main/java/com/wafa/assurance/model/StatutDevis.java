package com.wafa.assurance.model;

public enum StatutDevis {
    EN_ATTENTE("En attente"),
    ACCORDE("Accordé"),
    REFUSE("Refusé");

    private final String libelle;

    StatutDevis(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
