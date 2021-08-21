package com.example.api.task;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "task")
public class Task {
    @Id
    private String id;

    private String teamId;

    private String name;
    private String details;
    private boolean isOpen = true;
    private String lastUserId;
    private String authorId;
    private String responsibleId;
    private boolean isAssigned = false;
    private boolean isPrivate = false;
}
