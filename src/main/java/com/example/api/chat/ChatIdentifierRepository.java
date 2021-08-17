package com.example.api.chat;

import com.example.api.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatIdentifierRepository extends MongoRepository<ChatIdentifier, String> {

    Optional<ChatIdentifier> findByPartnerOneAndPartnerTwo(String partnerOne, String partnerTwo);
}
