package com.example.api.task;



import com.example.api.user.User;
import com.example.api.user.UserController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "task/api")
public class TaskController {


    private final UserController userController = new UserController();

    private final User LEADER = userController.getUser(1);

    private final List<Task> TASKS = Arrays.asList(
            new Task(1,"Verification","Verify that", LEADER),
            new Task(2,"Go home","Hurry up and go home", LEADER),
            new Task(2,"Create a new project","Create a new project with Java", LEADER)
    );


    public Task getTask(Integer taskId)
    {
        return TASKS.stream().filter(task -> taskId.equals(task.getTaskId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Task with " + taskId + " does not exists"));
    }

    @PostMapping(path = "{taskId}/assignToMembers")
    @PreAuthorize("hasAuthority('task:assign')")
    public List<User> assignTaskToUsers(@PathVariable("taskId") Integer taskId, @RequestBody List<User> users)
    {
        users.forEach(user -> user.setTask(getTask(taskId)));
        return users;
    }

    @PostMapping(path = "new")
    @PreAuthorize("hasAuthority('task:create')")
    public void createTask(@RequestBody Task task)
    {
        TASKS.add(task);
    }

    @DeleteMapping(path = "delete/{taskId}")
    @PreAuthorize("hasAuthority('task:delete')")
    public void deleteTask(Integer taskId)
    {
        TASKS.removeIf(task -> task.getTaskId().equals(taskId));
    }
}
