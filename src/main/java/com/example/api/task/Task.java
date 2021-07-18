package com.example.api.task;

import com.example.api.user.User;

public class Task {
    private final Integer taskId;
    private final String taskName;
    private final String taskDetails;
    private final User taskCreator;


    public Task(Integer taskId, String taskName, String taskDetails, User taskCreator) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDetails = taskDetails;
        this.taskCreator = taskCreator;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDetails='" + taskDetails + '\'' +
                '}';
    }
}
