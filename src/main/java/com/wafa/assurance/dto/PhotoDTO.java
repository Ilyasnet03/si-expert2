package com.wafa.assurance.dto;

import com.wafa.assurance.model.Photo;
import com.wafa.assurance.model.CategoriePhoto;
import com.wafa.assurance.model.TypePhoto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PhotoDTO {
    private Long id;
    private Long missionId;
    private String url;
    private CategoriePhoto categorie;
    private TypePhoto type;
    private String description;
    private LocalDateTime dateUpload;

    public static PhotoDTO fromEntity(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setId(photo.getId());
        dto.setMissionId(photo.getMission().getId());
        dto.setUrl(photo.getUrl());
        dto.setCategorie(photo.getCategorie());
        dto.setType(photo.getType());
        dto.setDescription(photo.getDescription());
        dto.setDateUpload(photo.getDateUpload());
        return dto;
    }
}
