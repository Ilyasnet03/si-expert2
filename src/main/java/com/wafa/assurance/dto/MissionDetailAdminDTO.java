package com.wafa.assurance.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MissionDetailAdminDTO {
    private Long id;
    private String numeroMission;
    private String typeSinistre;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereActivite;
    private SinistreDTO sinistre;
    private List<ExpertiseDTO> expertises;
    private List<DevisDTO> devis;
    private List<PhotoDTO> photos;
    private List<NoteHonoraireDTO> notesHonoraire;
    private List<String> etapes;
    private int progression;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroMission() { return numeroMission; }
    public void setNumeroMission(String numeroMission) { this.numeroMission = numeroMission; }
    public String getTypeSinistre() { return typeSinistre; }
    public void setTypeSinistre(String typeSinistre) { this.typeSinistre = typeSinistre; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDerniereActivite() { return derniereActivite; }
    public void setDerniereActivite(LocalDateTime derniereActivite) { this.derniereActivite = derniereActivite; }
    public SinistreDTO getSinistre() { return sinistre; }
    public void setSinistre(SinistreDTO sinistre) { this.sinistre = sinistre; }
    public List<ExpertiseDTO> getExpertises() { return expertises; }
    public void setExpertises(List<ExpertiseDTO> expertises) { this.expertises = expertises; }
    public List<DevisDTO> getDevis() { return devis; }
    public void setDevis(List<DevisDTO> devis) { this.devis = devis; }
    public List<PhotoDTO> getPhotos() { return photos; }
    public void setPhotos(List<PhotoDTO> photos) { this.photos = photos; }
    public List<NoteHonoraireDTO> getNotesHonoraire() { return notesHonoraire; }
    public void setNotesHonoraire(List<NoteHonoraireDTO> notesHonoraire) { this.notesHonoraire = notesHonoraire; }
    public List<String> getEtapes() { return etapes; }
    public void setEtapes(List<String> etapes) { this.etapes = etapes; }
    public int getProgression() { return progression; }
    public void setProgression(int progression) { this.progression = progression; }
}
