package com.example.api.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {

    Optional<List<Chat>> findChatBySenderIdOrReceiverId(String senderId, String receiverId);
    Optional<List<Chat>> findChatByChatId(String chatId);


}
