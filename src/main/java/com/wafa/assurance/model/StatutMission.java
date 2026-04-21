package com.wafa.assurance.model;

public enum StatutMission {
    NOUVELLE("Nouvelle mission"),
    ACCEPTEE("Mission acceptée"),
    EN_COURS("Mission en cours"),
    NON_CLOTUREE("Mission non clôturée"),
    REFUSEE("Mission refusée"),
    CARENCE("Mission en carence"),
    REEXAMEN("Mission en réexamen"),
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
            case "ACCEPTEE" -> ACCEPTEE;
            case "EN_COURS" -> EN_COURS;
            case "NON_CLOTUREE" -> NON_CLOTUREE;
            case "REFUSEE" -> REFUSEE;
            case "CARENCE" -> CARENCE;
            case "REEXAMEN" -> REEXAMEN;
            case "HONORAIRES" -> HONORAIRES;
            default -> throw new IllegalArgumentException("Corbeille inconnue: " + corbeille);
        };
    }
}