package com.example.api.chat;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public Chat saveChat(Chat chat) {
        return this.chatRepository.save(chat);
    }

    public List<Chat> getChat(String requesterId, String partnerId) {
        List<Chat> chats = new ArrayList<>();
        List<Chat> senderToPartner = this.chatRepository.findChatBySenderIdAndReceiverId(requesterId, partnerId).orElseThrow(() -> new IllegalStateException("Chat not found"));
        List<Chat> partnerToSender = this.chatRepository.findChatBySenderIdAndReceiverId(partnerId, requesterId).orElseThrow(() -> new IllegalStateException("Chat not found"));
        chats.addAll(senderToPartner);
        chats.addAll(partnerToSender);
        return chats;
    }
}
