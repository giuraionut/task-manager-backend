package com.example.api.task;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.response.Response;
import com.example.api.team.Team;
import com.example.api.team.TeamService;
import com.example.api.user.User;
import com.example.api.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SecretKey secretKey;
    private final UserService userService;
    private final TeamService teamService;
    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "new")
    @PreAuthorize("hasAuthority('task:create')")
    public ResponseEntity<Object> newTask(@RequestBody Task task, HttpServletRequest request) {

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        String requesterId = authorVerifier.getRequesterId();
        if (requesterId != null) {
            task.setAuthorId(requesterId);
            task.setOpen(true);
            this.taskService.createTask(task);

            response.setStatus(HttpStatus.CREATED);
            response.setError("none");
            response.setMessage("Task created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.setStatus(HttpStatus.CREATED);
            response.setError("no requesterId");
            response.setMessage("Requester ID not found");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(path = "public")
    @PreAuthorize("hasAuthority('task:delete')")
    public ResponseEntity<Object> deleteTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.taskService.exists(task.getId())) {
            String authorId = this.taskService.getTask(task.getId()).getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isAuthor()) {
                response.setStatus(HttpStatus.NO_CONTENT);
                response.setError("none");
                response.setMessage("Task deleted successfully");
                this.taskService.deleteTask(task);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            } else {
                response.setStatus(HttpStatus.NO_CONTENT);
                response.setError("not authorized");
                response.setMessage("Can't delete other tasks");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "public")
    @PreAuthorize("hasAuthority('task:edit')")
    public ResponseEntity<Object> editTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.taskService.exists(task.getId())) {

            Task originalTask = this.taskService.getTask(task.getId());

            String authorId = originalTask.getAuthorId();
            String responsibleId = originalTask.getResponsibleId();

            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            AuthorVerifier responsibleVerifier = new AuthorVerifier(request, secretKey, responsibleId);
            if (authorVerifier.isAuthor()) {
                task.setLastUserId(authorId);
            } else if (responsibleVerifier.isAuthor()) {
                task.setLastUserId(responsibleId);
            } else {
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setError("not authorized");
                response.setMessage("You are not authorized to edit this task");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("Task edited successfully");

            this.taskService.updateTask(task);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(path = "quantity/{type}/{state}")
    public ResponseEntity<Object> countTasks(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("state") String state) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setMessage("Task quantity obtained successfully");
        response.setError("none");
        response.setStatus(HttpStatus.OK);
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        Integer quantity = this.taskService.countTasks(authorVerifier.getRequesterId(), type, state);
        response.setPayload(quantity);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "{type}")
    public ResponseEntity<Object> getTasks(HttpServletRequest request, @PathVariable("type") String type) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        List<Task> tasks = this.taskService.getTasks(authorVerifier.getRequesterId(), type);
        if (!tasks.isEmpty()) {
            response.setStatus(HttpStatus.OK);
            response.setMessage("Tasks obtained");
            response.setError("none");
            response.setPayload(tasks);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage("No tasks found for user");
            response.setError("not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


//------------------------------------------------------------------------------------------------------------------

    @PostMapping(path = "private")
    @PreAuthorize("hasAuthority('task:private-create')")
    public ResponseEntity<Object> newPrivateTask(@RequestBody Task task, HttpServletRequest request) {

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        String requesterId = authorVerifier.getRequesterId();
        if (requesterId != null) {
            Task newTask = this.taskService.createPrivateTask(task, requesterId);
            response.setStatus(HttpStatus.CREATED);
            response.setError("none");
            response.setMessage("Task created successfully");
            response.setPayload(newTask);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.setStatus(HttpStatus.CREATED);
            response.setError("no requesterId");
            response.setMessage("Requester ID not found");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(path = "private")
    @PreAuthorize("hasAuthority('task:private-delete')")
    public ResponseEntity<Object> deletePrivateTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (taskService.exists(task.getId())) {
            String authorId = this.taskService.getTask(task.getId()).getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isAuthor()) {
                response.setStatus(HttpStatus.NO_CONTENT);
                response.setError("none");
                response.setMessage("Task deleted successfully");
                taskService.deleteTask(task);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            } else {
                response.setStatus(HttpStatus.NO_CONTENT);
                response.setError("not authorized");
                response.setMessage("Can't delete other tasks");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "private")
    @PreAuthorize("hasAuthority('task:private-edit')")
    public ResponseEntity<Object> editPrivateTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.taskService.exists(task.getId())) {

            Task originalTask = this.taskService.getTask(task.getId());

            String authorId = originalTask.getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isAuthor()) {
                task.setLastUserId(authorId);
            } else {
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setError("not authorized");
                response.setMessage("You are not authorized to edit this task");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("Task edited successfully");
            this.taskService.updateTask(task);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
