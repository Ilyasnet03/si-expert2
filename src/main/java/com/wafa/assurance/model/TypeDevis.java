package com.wafa.assurance.model;

public enum TypeDevis {
    INITIAL("Devis initial"),
    COMPLEMENTAIRE("Devis complémentaire"),
    CONTRADICTOIRE("Devis contradictoire");

    private final String libelle;

    TypeDevis(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
