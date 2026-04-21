package com.wafa.assurance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppNotificationDTO {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String resourceUrl;
    private boolean read;
    private LocalDateTime createdAt;
}