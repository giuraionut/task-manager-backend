package com.example.api.task;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.response.Response;
import com.example.api.team.Team;
import com.example.api.team.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SecretKey secretKey;
    private final TeamService teamService;

    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(path = "public")
    @PreAuthorize("hasAuthority('task:delete')")
    public ResponseEntity<Object> deleteTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        if (this.taskService.exists(task.getId())) {
            String authorId = this.taskService.getTask(task.getId()).getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);
            if (authorVerifier.isValid()) {
                response.setError("none");
                response.setMessage("Task deleted successfully");
                this.taskService.deleteTask(task);
            } else {
                response.setError("not authorized");
                response.setMessage("Can't delete other tasks");
            }
        } else {
            response.setError("not found");
            response.setMessage("Task does not exists");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "public")
    @PreAuthorize("hasAuthority('task:public-edit')")
    public ResponseEntity<Object> editTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        if (this.taskService.exists(task.getId())) {
            Task originalTask = this.taskService.getTask(task.getId());
            String authorId = originalTask.getAuthorId();
            String responsibleId = originalTask.getResponsibleId();

            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);
            AuthorVerifier responsibleVerifier = new AuthorVerifier(request, secretKey, responsibleId);

            if (responsibleVerifier.isValid()) {
                task.setLastUserId(responsibleId);
            } else if (authorVerifier.isValid()) {
                task.setLastUserId(authorId);
            } else {
                response.setError("not authorized");
                response.setMessage("You are not authorized to edit this task");
            }
            response.setError("none");
            response.setMessage("Task edited successfully");
            this.taskService.updateTask(task);
        } else {
            response.setError("not found");
            response.setMessage("Task does not exists");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(path = "quantity/{type}/{state}")
    public ResponseEntity<Object> countTasks(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("state") String state) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setMessage("Task [" + type + " " + state + "] quantity obtained successfully");
        response.setError("none");
        response.setStatus(HttpStatus.OK);
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        Integer quantity = this.taskService.countTasks(authorVerifier.getRequesterId(), type, state);
        response.setPayload(quantity);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "{type}")
    public ResponseEntity<Object> getTasks(HttpServletRequest request, @PathVariable("type") String type) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        List<Task> tasks = this.taskService.getTasks(authorVerifier.getRequesterId(), type);
        if (!tasks.isEmpty()) {
            response.setMessage("Tasks [" + type + "] obtained successfully");
            response.setError("none");
            response.setPayload(tasks);
        } else {
            response.setMessage("No tasks found for user");
            response.setError("not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "private")
    @PreAuthorize("hasAuthority('task:private-create')")
    public ResponseEntity<Object> newPrivateTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);

        String requesterId = authorVerifier.getRequesterId();
        if (requesterId != null) {
            Task newTask = this.taskService.createTask(task, requesterId, requesterId);
            response.setError("none");
            response.setMessage("Task created successfully");
            response.setPayload(newTask);
        } else {
            response.setError("no requesterId");
            response.setMessage("Requester ID not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "public/{userId}")
    @PreAuthorize("hasAuthority('task:public-create')")
    public ResponseEntity<Object> newPublicTask(@RequestBody Task task, @PathVariable("userId") String userId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String requesterId = authorVerifier.getRequesterId();
        Team team = this.teamService.getTeamByAuthor(requesterId);

        if (requesterId != null) {
            if (team.getMembersId().contains(userId)) {
                Task newTask = this.taskService.createTask(task, userId, requesterId);
                response.setError("none");
                response.setMessage("Task created successfully");
                response.setPayload(newTask);
            } else {
                response.setError("wrong team member");
                response.setMessage("Can't assign task to members from other teams");
            }
        } else {
            response.setError("no requesterId");
            response.setMessage("Requester ID not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(path = "private")
    @PreAuthorize("hasAuthority('task:private-delete')")
    public ResponseEntity<Object> deletePrivateTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        String authorId = this.taskService.getTask(task.getId()).getAuthorId();
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

        if (taskService.exists(task.getId())) {

            if (authorVerifier.isValid()) {
                response.setError("none");
                response.setMessage("Task deleted successfully");
                taskService.deleteTask(task);
            } else {
                response.setError("not authorized");
                response.setMessage("Can't delete other tasks");
            }
        } else {
            response.setError("not found");
            response.setMessage("Task does not exists");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //fa delete public task
    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "private")
    @PreAuthorize("hasAuthority('task:private-edit')")
    public ResponseEntity<Object> editPrivateTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        if (this.taskService.exists(task.getId())) {

            Task originalTask = this.taskService.getTask(task.getId());

            String authorId = originalTask.getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isValid()) {
                task.setLastUserId(authorId);
            } else {
                response.setError("not authorized");
                response.setMessage("You are not authorized to edit this task");
            }
            response.setError("none");
            response.setMessage("Task edited successfully");
            this.taskService.updateTask(task);

        } else {
            response.setError("not found");
            response.setMessage("Task does not exists");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
