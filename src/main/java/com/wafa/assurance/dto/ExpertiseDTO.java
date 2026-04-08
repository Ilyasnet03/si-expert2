package com.wafa.assurance.dto;

import com.wafa.assurance.model.Expertise;
import com.wafa.assurance.model.TypeExpertise;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExpertiseDTO {
    private Long id;
    private Long missionId;
    private TypeExpertise typeExpertise;
    private LocalDate dateExpertise;
    private String lieu;
    private Integer kilometrage;
    private String etatVehicule;
    private String estimationDommages;
    private Double montantEstimation;
    private String calculVVADE;
    private String arbitrage;
    private String expertiseContradictoire;
    private LocalDate dateExpertiseContradictoire;
    private Double montantExpertiseContradictoire;
    private String observations;
    private LocalDateTime dateCreation;

    public static ExpertiseDTO fromEntity(Expertise expertise) {
        ExpertiseDTO dto = new ExpertiseDTO();
        dto.setId(expertise.getId());
        dto.setMissionId(expertise.getMission().getId());
        dto.setTypeExpertise(expertise.getTypeExpertise());
        dto.setDateExpertise(expertise.getDateExpertise());
        dto.setLieu(expertise.getLieu());
        dto.setKilometrage(expertise.getKilometrage());
        dto.setEtatVehicule(expertise.getEtatVehicule());
        dto.setEstimationDommages(expertise.getEstimationDommages());
        dto.setMontantEstimation(expertise.getMontantEstimation());
        dto.setCalculVVADE(expertise.getCalculVVADE());
        dto.setArbitrage(expertise.getArbitrage());
        dto.setExpertiseContradictoire(expertise.getExpertiseContradictoire());
        dto.setDateExpertiseContradictoire(expertise.getDateExpertiseContradictoire());
        dto.setMontantExpertiseContradictoire(expertise.getMontantExpertiseContradictoire());
        dto.setObservations(expertise.getObservations());
        dto.setDateCreation(expertise.getDateCreation());
        return dto;
    }
}
