package com.example.api.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return this.notificationRepository.findNotificationByReceiverId(receiverId).orElseThrow(() -> new IllegalStateException("Receiver ID " + receiverId + " does not exists"));
    }

    public void delete(Notification notification) {
        this.notificationRepository.delete(notification);
    }
}
