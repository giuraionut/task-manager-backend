package com.example.api.notification;

import lombok.Data;
import org.springframework.data.annotation.Id;


import java.time.LocalDateTime;

@Data
public class Notification {

    @Id
    private String id;

    private String content;
    private String senderId;
    private String receiverId;
    private LocalDateTime timestamp;
}
