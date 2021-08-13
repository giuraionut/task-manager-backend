package com.example.api.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository()
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByRefreshToken(String refreshToken);
}
