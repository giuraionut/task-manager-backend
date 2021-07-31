package com.example.api.team;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository(value = "team")
public interface TeamRepository extends MongoRepository<Team, String> {
    Optional<Team> findByName(String name);
}
