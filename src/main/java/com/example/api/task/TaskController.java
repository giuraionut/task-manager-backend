package com.example.api.task;

import com.example.api.response.Response;
import com.example.api.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @PostMapping(path = "new")
    @PreAuthorize("hasAuthority('task:create')")
    public ResponseEntity<Object> newTask(@RequestBody Task task) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.CREATED);
        response.setError("none");
        response.setMessage("Task created successfully");
        this.taskService.createTask(task);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "delete/{taskId}")
    @PreAuthorize("hasAuthority('task:delete')")
    public ResponseEntity<Object> deleteTask(@PathVariable("taskId") String taskId) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (taskService.exists(taskId)) {
            response.setStatus(HttpStatus.NO_CONTENT);
            response.setError("none");
            response.setMessage("Task deleted successfully");
            taskService.deleteTask(taskId);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "edit/{taskId}")
    @PreAuthorize("hasAuthority('task:edit')")
    public ResponseEntity<Object> editTask(@RequestBody Task task, @PathVariable("taskId") String taskId) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (taskService.exists(taskId)) {
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("Task edited successfully");
            taskService.editTask(task, taskId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "assign/{taskId}/{userId}")
    @PreAuthorize("hasAuthority('task:assign')")
    public ResponseEntity<Object> assignTask(@PathVariable("taskId") String taskId, @PathVariable("userId") String userId) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (taskService.exists(taskId) && userService.exists(userId)) {
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("Task assigned successfully");
            taskService.assignTask(taskId, userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task or user does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
