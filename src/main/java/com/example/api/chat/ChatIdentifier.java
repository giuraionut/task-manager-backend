package com.example.api.chat;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ChatIdentifier {
    @Id
    private String id;

    private String partnerOne;
    private String partnerTwo;
}
