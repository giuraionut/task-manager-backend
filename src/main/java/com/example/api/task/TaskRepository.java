package com.example.api.task;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "task")
public interface TaskRepository extends MongoRepository<Task, String> {

}
