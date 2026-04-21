package com.wafa.assurance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafa.assurance.dto.AppNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationCenterService {

    private final AtomicLong idSequence = new AtomicLong(1);
    private final List<AppNotificationDTO> notifications = new ArrayList<>();
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Autowired
    private ObjectMapper objectMapper;

    public synchronized List<AppNotificationDTO> list() {
        return notifications.stream()
            .sorted(Comparator.comparing(AppNotificationDTO::getCreatedAt).reversed())
            .toList();
    }

    public synchronized AppNotificationDTO publish(String type, String title, String message, String resourceUrl) {
        AppNotificationDTO notification = new AppNotificationDTO(
            idSequence.getAndIncrement(),
            type,
            title,
            message,
            resourceUrl,
            false,
            LocalDateTime.now()
        );
        notifications.add(notification);
        if (notifications.size() > 100) {
            notifications.remove(0);
        }
        broadcast(notification);
        return notification;
    }

    public synchronized void markRead(Long id) {
        notifications.stream()
            .filter(notification -> notification.getId().equals(id))
            .findFirst()
            .ifPresent(notification -> notification.setRead(true));
    }

    public synchronized void markAllRead() {
        notifications.forEach(notification -> notification.setRead(true));
    }

    public void register(WebSocketSession session) {
        sessions.add(session);
    }

    public void unregister(WebSocketSession session) {
        sessions.remove(session);
    }

    private void broadcast(AppNotificationDTO notification) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            return;
        }

        sessions.removeIf(session -> !session.isOpen());
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                try {
                    session.close();
                } catch (IOException ignored) {
                }
            }
        });
    }
}