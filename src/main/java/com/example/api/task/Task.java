package com.example.api.task;

import com.example.api.user.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "task")
public class Task {
    @Id
    private String id;

    private String taskName;
    private String taskDetails;
    private User emitter;

    public Task(String taskName, String taskDetails, User emitter) {
        this.taskName = taskName;
        this.taskDetails = taskDetails;
        this.emitter = emitter;
    }
}
