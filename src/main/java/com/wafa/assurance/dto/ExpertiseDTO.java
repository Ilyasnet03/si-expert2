package com.wafa.assurance.dto;

import com.wafa.assurance.model.Expertise;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExpertiseDTO {
    private Long id;
    private Long missionId;
    private LocalDate dateExpertise;
    private String lieu;
    private Integer kilometrage;
    private String etatVehicule;
    private String estimationDommages;
    private BigDecimal montantEstimation;
    private String calculVVADE;
    private String arbitrage;
    private String expertiseContradictoire;
    private LocalDate dateExpertiseContradictoire;
    private BigDecimal montantExpertiseContradictoire;
    private String observations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExpertiseDTO from(Expertise expertise) {
        ExpertiseDTO dto = new ExpertiseDTO();
        dto.setId(expertise.getId());
        dto.setMissionId(expertise.getMission().getId());
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
        dto.setCreatedAt(expertise.getCreatedAt());
        dto.setUpdatedAt(expertise.getUpdatedAt());
        return dto;
    }
}
