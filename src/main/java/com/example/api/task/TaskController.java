package com.example.api.task;



import com.example.api.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "task/api")
public class TaskController {


    private static final List<Task> TASKS = Arrays.asList(
            new Task(1,"Verification","Verify that"),
            new Task(2,"Go home","Hurry up and go home"),
            new Task(2,"Create a new project","Create a new project with Java")
    );

    @GetMapping(path = "{taskId}")
    public static Task getTask(@PathVariable("taskId") Integer taskId)
    {
        return TASKS.stream().filter(task -> taskId.equals(task.getTaskId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Task with " + taskId + " does not exists"));
    }

    @PostMapping(path = "{taskId}/assignToMembers")
    @PreAuthorize("hasRole('ROLE_LEADER')")
    public static List<User> assignTaskToUsers(@PathVariable("taskId") Integer taskId, @RequestBody List<User> users)
    {
        users.forEach(user -> user.setTask(TaskController.getTask(taskId)));
        return users;
    }
}
