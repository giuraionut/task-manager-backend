package com.example.api.chat;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatIdentifierRepository chatIdentifierRepository;

    public Chat saveChat(Chat chat) {


        Optional<ChatIdentifier> chatIdentifier = this.chatIdentifierRepository.findByPartnerOneAndPartnerTwo(chat.getSenderId(), chat.getReceiverId());
        Optional<ChatIdentifier> chatIdentifier2 = this.chatIdentifierRepository.findByPartnerOneAndPartnerTwo(chat.getReceiverId(), chat.getSenderId());

        chatIdentifier.ifPresent(identifier -> chat.setChatId(identifier.getId()));
        chatIdentifier2.ifPresent(identifier -> chat.setChatId(identifier.getId()));

        if (chatIdentifier.isEmpty() && chatIdentifier2.isEmpty()) {
            ChatIdentifier chatId = new ChatIdentifier();
            chatId.setPartnerOne(chat.getSenderId());
            chatId.setPartnerTwo(chat.getReceiverId());
            String id = this.chatIdentifierRepository.save(chatId).getId();
            chat.setChatId(id);
        }
        return this.chatRepository.save(chat);
    }

    public List<Chat> getChat(String requesterId, String partnerId) {
        List<Chat> chats = new ArrayList<>();

        Optional<ChatIdentifier> chatIdentifier = this.chatIdentifierRepository.findByPartnerOneAndPartnerTwo(requesterId, partnerId);
        Optional<ChatIdentifier> chatIdentifier2 = this.chatIdentifierRepository.findByPartnerOneAndPartnerTwo(partnerId, requesterId);

        if (chatIdentifier.isPresent()) {
            Optional<List<Chat>> chats1 = this.chatRepository.findChatByChatId(chatIdentifier.get().getId());
            if (chats1.isPresent()) {
                chats = chats1.get();
            }
        } else if (chatIdentifier2.isPresent()) {
            Optional<List<Chat>> chats2 = this.chatRepository.findChatByChatId(chatIdentifier2.get().getId());
            if (chats2.isPresent()) {
                chats = chats2.get();
            }
        }

        return chats;
    }
}
