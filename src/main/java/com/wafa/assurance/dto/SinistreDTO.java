package com.wafa.assurance.dto;

import com.wafa.assurance.model.Mission;
import com.wafa.assurance.model.StatutMission;
import java.time.LocalDateTime;

public class SinistreDTO {
    private Long id;
    private String refSinistre;
    private String numPolice;
    private String immatriculation;
    private String typeMission;
    private String parcours;
    private String telAssure;
    private StatutMission statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateCloture;
    private String observations;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRefSinistre() { return refSinistre; }
    public void setRefSinistre(String refSinistre) { this.refSinistre = refSinistre; }
    public String getNumPolice() { return numPolice; }
    public void setNumPolice(String numPolice) { this.numPolice = numPolice; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public String getTypeMission() { return typeMission; }
    public void setTypeMission(String typeMission) { this.typeMission = typeMission; }
    public String getParcours() { return parcours; }
    public void setParcours(String parcours) { this.parcours = parcours; }
    public String getTelAssure() { return telAssure; }
    public void setTelAssure(String telAssure) { this.telAssure = telAssure; }
    public StatutMission getStatut() { return statut; }
    public void setStatut(StatutMission statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDateTime dateCloture) { this.dateCloture = dateCloture; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    // Méthode de conversion statique
    public static SinistreDTO from(Mission mission) {
        SinistreDTO dto = new SinistreDTO();
        dto.setId(mission.getId());
        dto.setRefSinistre(mission.getRefSinistre());
        dto.setNumPolice(mission.getNumPolice());
        dto.setImmatriculation(mission.getImmatriculation());
        dto.setTypeMission(mission.getTypeMission());
        dto.setParcours(mission.getParcours());
        dto.setTelAssure(mission.getTelAssure());
        dto.setStatut(mission.getStatut());
        dto.setDateCreation(mission.getDateCreation());
        dto.setDateCloture(mission.getDateCloture());
        dto.setObservations(mission.getObservations());
        return dto;
    }
}
