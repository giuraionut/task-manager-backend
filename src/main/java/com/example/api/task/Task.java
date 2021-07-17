package com.example.api.task;

public class Task {
    public final Integer taskId;
    public final String taskName;
    public final String taskDetails;


    public Task(Integer taskId, String taskName, String taskDetails) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDetails = taskDetails;
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

}
