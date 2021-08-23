package com.example.api.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public boolean exists(String taskId) {
        return this.taskRepository.findById(taskId).isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------
    public Task getTask(String taskId) {
        Optional<Task> taskById = taskRepository.findById(taskId);
        return taskById.orElse(null);
    }

    //------------------------------------------------------------------------------------------------------------------
    public List<Task> getPrivateTasks(String userId) {
        Optional<List<Task>> taskByResponsibleId = taskRepository.findTaskByResponsibleId(userId);
        return taskByResponsibleId.map(tasks -> tasks.stream().filter(Task::isPrivate).collect(Collectors.toList())).orElse(null);
    }

    public Integer countTasks(String responsibleId, String type, String state) {
        return this.taskRepository.countByResponsibleIdAndIsPrivateAndIsOpen(responsibleId, type.equals("private"), state.equals("open"));
    }

    public List<Task> getTaskByTeam(String teamId) {
        Optional<List<Task>> taskByTeamId = this.taskRepository.findTaskByTeamId(teamId);
        return taskByTeamId.orElse(null);
    }
}
