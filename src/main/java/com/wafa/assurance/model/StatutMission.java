package com.wafa.assurance.model;

public enum StatutMission {
    NOUVELLE("Nouvelle mission"),
    NON_CLOTUREE("Mission non clôturée"),
    REFUSEE("Mission refusée"),
    CARENCE("Mission en carence"),
    HONORAIRES("Notes d'honoraires"),
    CLOTUREE("Mission clôturée");

    private final String libelle;

    StatutMission(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    // Pour le mapping avec les corbeilles du dashboard
    public static StatutMission fromCorbeille(String corbeille) {
        return switch (corbeille) {
            case "NOUVELLE" -> NOUVELLE;
            case "NON_CLOTUREE" -> NON_CLOTUREE;
            case "REFUSEE" -> REFUSEE;
            case "CARENCE" -> CARENCE;
            case "HONORAIRES" -> HONORAIRES;
            default -> throw new IllegalArgumentException("Corbeille inconnue: " + corbeille);
        };
    }
}