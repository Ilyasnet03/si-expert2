package com.wafa.assurance.dto;

import com.wafa.assurance.model.CategoriePhoto;
import com.wafa.assurance.model.Photo;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PhotoDTO {
    private Long id;
    private Long missionId;
    private CategoriePhoto categorie;
    private String cheminFichier;
    private String nomOriginal;
    private Long tailleFichier;
    private LocalDateTime createdAt;

    public static PhotoDTO from(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setId(photo.getId());
        dto.setMissionId(photo.getMission().getId());
        dto.setCategorie(photo.getCategorie());
        dto.setCheminFichier(photo.getCheminFichier());
        dto.setNomOriginal(photo.getNomOriginal());
        dto.setTailleFichier(photo.getTailleFichier());
        dto.setCreatedAt(photo.getCreatedAt());
        return dto;
    }
}
