package com.wafa.assurance.controller;

import com.wafa.assurance.dto.AppNotificationDTO;
import com.wafa.assurance.service.NotificationCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "${app.frontend-url:http://localhost:3000}")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationCenterService notificationCenterService;

    @GetMapping
    public ResponseEntity<List<AppNotificationDTO>> list() {
        return ResponseEntity.ok(notificationCenterService.list());
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationCenterService.markRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        notificationCenterService.markAllRead();
        return ResponseEntity.noContent().build();
    }
}