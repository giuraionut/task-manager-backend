package com.example.api.task;

import com.example.api.user.User;
import com.example.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(@Qualifier("task") TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public void createTask(Task task) {
        this.taskRepository.insert(task);
    }

    @Transactional
    public void deleteTask(String taskId) {
        this.taskRepository.deleteById(taskId);
    }

    @Transactional
    public void editTask(Task task, String taskId) {
        Task editedTask = taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task with id " + taskId + " does not exists!"));
        editedTask = task;
    }

    @Transactional
    public void assignTask(String taskId, String userId) {

        User assignedUser = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exists!"));

        List<String> usersTasks = assignedUser.getTasksId();
        usersTasks.add(taskId);
    }

    public boolean exists(String taskId) {
        return this.taskRepository.findById(taskId).isPresent();
    }
}
