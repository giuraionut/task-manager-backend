package com.example.api.task;

import com.example.api.user.User;
import com.example.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Autowired
    public TaskService(@Qualifier("task") TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
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
        Task editedTask = getTaskById(taskId);
        editedTask.setTaskDetails(task.getTaskDetails());
        editedTask.setTaskName(task.getTaskName());
        this.taskRepository.save(editedTask);
    }

    @Transactional
    public void assignTask(String taskId, String userId) {
        User assignedUser = this.userService.getUserById(userId);
        Set<String> tasksId = assignedUser.getTasksId();
        tasksId.add(taskId);
        assignedUser.setTasksId(tasksId);
        this.userService.add(assignedUser);
    }

    public boolean exists(String taskId) {
        return this.taskRepository.findById(taskId).isPresent();
    }

    public Task getTaskById(String taskId)
    {
        return taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task with id " + taskId + " does not exists!"));
    }

    @Transactional
    public void closeTask(String taskId, String closedById) {
        Task task = getTaskById(taskId);
        task.setOpen(false);
        task.setLastUserId(closedById);
        this.taskRepository.save(task);
    }

    @Transactional
    public void openTask(String taskId, String openById) {
        Task task = getTaskById(taskId);
        task.setOpen(true);
        task.setLastUserId(openById);
        this.taskRepository.save(task);
    }
}
