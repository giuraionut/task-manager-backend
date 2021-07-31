package com.example.api.task;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository(value = "task")
public interface TaskRepository extends MongoRepository<Task, String> {

    Optional<List<Task>> findTaskByAuthorId(String authorId);

}
