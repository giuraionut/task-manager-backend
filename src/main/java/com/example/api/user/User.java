package com.example.api.user;

import com.example.api.task.Task;

import java.util.List;

public class User {
    private final Integer userId;
    private final String userName;
    private final List<Task> tasks;
    private final String email;
    private final String birthDate;
    private final String avatar;

    public User(Integer userId, String userName, List<Task> tasks, String email, String birthDate, String avatar) {
        this.userId = userId;
        this.userName = userName;
        this.tasks = tasks;
        this.email = email;
        this.birthDate = birthDate;
        this.avatar = avatar;
    }

    public String getEmail() {return email;  }

    public String getBirthDate() { return birthDate; }

    public String getAvatar() { return avatar; }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTask(Task task) {
        tasks.add(task);
    }
}

