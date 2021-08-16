package com.example.api.chat;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class Chat {
    @Id
    private String id;

    private String message;
    private String senderId;
    private String receiverId;
    private LocalDateTime timestamp;

}
