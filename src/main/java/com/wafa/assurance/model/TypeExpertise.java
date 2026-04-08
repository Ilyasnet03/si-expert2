package com.wafa.assurance.model;

public enum TypeExpertise {
    AVANT_REPARATION("Avant réparation"),
    CONTRADICTOIRE("Contradictoire"),
    ARBITRAGE("Arbitrage");

    private final String libelle;

    TypeExpertise(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}