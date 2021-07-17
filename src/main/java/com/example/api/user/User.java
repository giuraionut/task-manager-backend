package com.example.api.user;

import com.example.api.task.Task;

import java.util.List;

public class User {
    private final Integer userId;
    private final String userName;
    private final List<Task> tasks;

    public User(Integer userId, String userName, List<Task> tasks) {
        this.userId = userId;
        this.userName = userName;
        this.tasks = tasks;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<Task> getTasks()
    {
        return tasks;
    }

    public void setTask(Task task)
    {
        tasks.add(task);
    }
}

