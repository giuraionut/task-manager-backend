package com.example.api.task;

import com.example.api.user.User;
import com.example.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Autowired
    public TaskService(@Qualifier("task") TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void createTask(Task task) {
        this.taskRepository.insert(task);
    }

    public Task createPrivateTask(Task task, String userId) {

        task.setLastUserId(userId);
        task.setAuthorId(userId);
        task.setResponsibleId(userId);
        task.setOpen(true);
        task.setPrivate(true);
        task.setAssigned(true);
        Task newTask = this.taskRepository.insert(task);
        this.assignTask(newTask.getId(), userId);
        return newTask;
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
    @Transactional
    public void setTaskResponsible(String taskId, String userId) {
        Task task = getTask(taskId);
        task.setResponsibleId(userId);
        this.taskRepository.save(task);
    }

    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void assignTask(String taskId, String userId) {
        User assignedUser = this.userService.getUserById(userId);

        Set<String> tasksId = assignedUser.getTasksId();
        if (tasksId.isEmpty()) {
            tasksId = new HashSet<>();
        }
        tasksId.add(taskId);
        assignedUser.setTasksId(tasksId);
        setTaskResponsible(taskId, userId);
    }//------------------------------------------------------------------------------------------------------------------

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
