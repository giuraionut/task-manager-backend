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
    public void createPrivateTask(Task task, String userId) {
        String taskId = this.taskRepository.insert(task).getId();
        this.assignTask(taskId ,userId);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void deleteTask(String taskId) {
        Task task = getTask(taskId);
        String responsibleId = task.getResponsibleId();
        User user = this.userService.getUserById(responsibleId);
        user.getTasksId().remove(taskId);
        this.userService.update(user);
        this.taskRepository.deleteById(taskId);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void editTask(Task task, String taskId) {
        Task editedTask = getTask(taskId);
        editedTask.setDetails(task.getDetails());
        editedTask.setName(task.getName());
        this.taskRepository.save(editedTask);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void setTaskResponsible(String taskId, String userId) {
        Task task = getTask(taskId);
        task.setResponsibleId(userId);
        this.taskRepository.save(task);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void setTaskLastUser(String taskId, String userId) {
        Task task = getTask(taskId);
        task.setLastUserId(userId);
        this.taskRepository.save(task);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void assignTask(String taskId, String userId) {
        User assignedUser = this.userService.getUserById(userId);

        Set<String> tasksId = assignedUser.getTasksId();
        if (tasksId.isEmpty()) {
            tasksId = new HashSet<>();
            tasksId.add(taskId);
            assignedUser.setTasksId(tasksId);
        } else {
            tasksId.add(taskId);
            assignedUser.setTasksId(tasksId);
        }
        setTaskResponsible(taskId, userId);

        this.userService.update(assignedUser);
    }//------------------------------------------------------------------------------------------------------------------

    public boolean exists(String taskId) {
        return this.taskRepository.findById(taskId).isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------
    public Task getTask(String taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task with id " + taskId + " does not exists!"));
    }
    //------------------------------------------------------------------------------------------------------------------
    public List<Task> getPrivateTasks(String userId)
    {
        List<Task> tasksByUser =  taskRepository.findTaskByAuthorId(userId).orElseThrow(() -> new IllegalStateException("User with id " + userId + " has no private tasks"));
        return tasksByUser.stream().filter(Task::isPrivate).collect(Collectors.toList());
    }
    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void closeTask(String taskId, String closedById) {
        Task task = getTask(taskId);
        task.setOpen(false);
        task.setLastUserId(closedById);
        this.taskRepository.save(task);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void openTask(String taskId, String openById) {
        Task task = getTask(taskId);
        task.setOpen(true);
        task.setLastUserId(openById);
        this.taskRepository.save(task);
    }
    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void setAssigned(String taskId, Boolean value)
    {
        Task task = getTask(taskId);
        task.setAssigned(value);
        this.taskRepository.save(task);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void changeTaskStatus(String taskId, String modifiedById)
    {
        Task task = getTask(taskId);
        task.setOpen(!task.isOpen());
        task.setLastUserId(modifiedById);
        this.taskRepository.save(task);
    }
}
