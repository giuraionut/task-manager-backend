package com.example.api.notification;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    Optional<List<Notification>> findNotificationByReceiverId(String receiverId);
}
