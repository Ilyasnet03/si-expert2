package com.wafa.assurance.model;

public enum MotifRefus {
    CONFLIT_INTERETS("Conflit d'intérêts"),
    CHARGE_TRAVAIL("Charge de travail"),
    ZONE_GEOGRAPHIQUE("Zone géographique"),
    SPECIALITE_REQUISE("Spécialité requise"),
    AUTRE("Autre");

    private final String libelle;

    MotifRefus(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}