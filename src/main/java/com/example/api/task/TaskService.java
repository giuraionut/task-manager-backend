package com.example.api.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(@Qualifier("task") TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    //------------------------------------------------------------------------------------------------------------------
    public Task createTask(Task task, String responsibleId, String authorId) {
        task.setLastUserId(authorId);
        task.setAuthorId(authorId);
        task.setResponsibleId(responsibleId);
        task.setOpen(true);
        task.setAssigned(true);
        return this.taskRepository.insert(task);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void deleteTask(Task task) {
        this.taskRepository.deleteById(task.getId());
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void updateTask(Task task) {
        this.taskRepository.save(task);
    }

    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------

    public boolean exists(String taskId) {
        return this.taskRepository.findById(taskId).isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------
    public Task getTask(String taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task with id " + taskId + " does not exists!"));
    }

    //------------------------------------------------------------------------------------------------------------------
    public List<Task> getTasks(String userId, String type) {
        List<Task> tasksByUser = taskRepository.findTaskByResponsibleId(userId)
                .orElseThrow(() -> new IllegalStateException("User with id " + userId + " has no " + type + " tasks"));
        if (type.equals("private")) {
            return tasksByUser.stream().filter(Task::isPrivate).collect(Collectors.toList());
        }
        return tasksByUser.stream().filter(Predicate.not(Task::isPrivate)).collect(Collectors.toList());
    }

    public Integer countTasks(String responsibleId, String type, String state) {
        return this.taskRepository.countByResponsibleIdAndIsPrivateAndIsOpen(responsibleId, type.equals("private"), state.equals("open"));
    }
}
