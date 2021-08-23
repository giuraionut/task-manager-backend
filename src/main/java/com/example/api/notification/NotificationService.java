package com.example.api.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification add(Notification notification) {
        return this.notificationRepository.insert(notification);
    }

    public List<Notification> getAll(String receiverId) {
        Optional<List<Notification>> notificationByReceiverId = this.notificationRepository.findNotificationByReceiverId(receiverId);
        return notificationByReceiverId.orElse(null);
    }

    public void delete(Notification notification) {
        this.notificationRepository.delete(notification);
    }
}
