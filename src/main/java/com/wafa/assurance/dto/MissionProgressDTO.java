package com.wafa.assurance.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MissionProgressDTO {
    private Long id;
    private String numeroMission;
    private String titre;
    private String typeSinistre;
    private String statut;
    private int progression; // 0-100
    private List<String> etapes;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereActivite;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroMission() { return numeroMission; }
    public void setNumeroMission(String numeroMission) { this.numeroMission = numeroMission; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getTypeSinistre() { return typeSinistre; }
    public void setTypeSinistre(String typeSinistre) { this.typeSinistre = typeSinistre; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getProgression() { return progression; }
    public void setProgression(int progression) { this.progression = progression; }
    public List<String> getEtapes() { return etapes; }
    public void setEtapes(List<String> etapes) { this.etapes = etapes; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDerniereActivite() { return derniereActivite; }
    public void setDerniereActivite(LocalDateTime derniereActivite) { this.derniereActivite = derniereActivite; }
}
