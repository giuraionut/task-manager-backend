package com.example.api.task;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.response.Response;
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

@RestController
@RequestMapping(path = "task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final SecretKey secretKey;

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
    @DeleteMapping(path = "delete/{taskId}")
    @PreAuthorize("hasAuthority('task:delete')")
    public ResponseEntity<Object> deleteTask(@PathVariable("taskId") String taskId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (taskService.exists(taskId)) {
            String authorId = this.taskService.getTask(taskId).getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isAuthor()) {
                response.setStatus(HttpStatus.NO_CONTENT);
                response.setError("none");
                response.setMessage("Task deleted successfully");
                taskService.deleteTask(taskId);
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
    @PutMapping(path = "edit/{taskId}")
    @PreAuthorize("hasAuthority('task:edit')")
    public ResponseEntity<Object> editTask(@RequestBody Task task, @PathVariable("taskId") String taskId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.taskService.exists(taskId)) {

            Task originalTask = this.taskService.getTask(taskId);

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
            this.taskService.editTask(task, taskId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
//    @PutMapping(path = "assign/{taskId}/{userId}") // nu e buna
//    @PreAuthorize("hasAuthority('task:assign')")
//    public ResponseEntity<Object> assignTask(@PathVariable("taskId") String taskId, @PathVariable("userId") String userId, HttpServletRequest request) {
//
//        Response response = new Response();
//        response.setTimestamp(LocalDateTime.now());
//        if (this.taskService.exists(taskId) && this.userService.exists(userId)) {
//            Task task = this.taskService.getTask(taskId);
//            String authorId = task.getAuthorId();
//            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);
//            if (authorVerifier.isAuthor()) {
//                response.setStatus(HttpStatus.OK);
//                response.setError("none");
//                response.setMessage("Task assigned successfully");
//                this.taskService.assignTask(taskId, userId);
//                this.taskService.setAssigned(taskId, true);
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                response.setStatus(HttpStatus.FORBIDDEN);
//                response.setError("not authorized");
//                response.setMessage("You can only assign tasks created by you");
//                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//            }
//        } else {
//            response.setStatus(HttpStatus.NOT_FOUND);
//            response.setError("not found");
//            response.setMessage("Task or user does not exists");
//            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//        }
//    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "status/{taskId}/{modifiedById}")
    @PreAuthorize("hasAuthority('task:change-status')")
    public ResponseEntity<Object> changeTaskStatus(@PathVariable("taskId") String taskId, @PathVariable("modifiedById") String modifiedById, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.taskService.exists(taskId)) {

            Task task = this.taskService.getTask(taskId);

            String authorId = task.getAuthorId();
            String responsibleId = task.getResponsibleId();

            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);
            AuthorVerifier responsibleVerifier = new AuthorVerifier(request, secretKey, responsibleId);
            if (authorVerifier.isAuthor()) {
                task.setLastUserId(authorId);
            } else if (responsibleVerifier.isAuthor()) {
                task.setLastUserId(responsibleId);
            } else {
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setError("not authorized");
                response.setMessage("You are not authorized to close this task");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("Task closed successfully");
            this.taskService.changeTaskStatus(taskId, modifiedById);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task with id " + taskId + " does not exists!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "details/{taskId}")
    public ResponseEntity<Object> getTask(@PathVariable("taskId") String taskId, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        Task task = this.taskService.getTask(taskId);
        String authorId = task.getAuthorId();

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

        if (authorVerifier.isAuthor()) {
            response.setStatus(HttpStatus.OK);
            response.setMessage("Task obtained");
            response.setError("none");
            response.setPayload(task);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.FORBIDDEN);
            response.setMessage("Can't access other tasks");
            response.setError("not authorized");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "private")
    public ResponseEntity<Object> getPrivateTasks(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        List<Task> tasks = this.taskService.getPrivateTasks(authorVerifier.getRequesterId());
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

    @PostMapping(path = "new/private")
    @PreAuthorize("hasAuthority('task:private-create')")
    public ResponseEntity<Object> newPrivateTask(@RequestBody Task task, HttpServletRequest request) {

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        String requesterId = authorVerifier.getRequesterId();
        if (requesterId != null) {
            task.setAuthorId(requesterId);
            task.setOpen(true);
            task.setPrivate(true);
            task.setAssigned(true);
            this.taskService.createPrivateTask(task, requesterId);
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
    @DeleteMapping(path = "delete/private/{taskId}")
    @PreAuthorize("hasAuthority('task:private-delete')")
    public ResponseEntity<Object> deletePrivateTask(@PathVariable("taskId") String taskId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (taskService.exists(taskId)) {
            String authorId = this.taskService.getTask(taskId).getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isAuthor()) {
                response.setStatus(HttpStatus.NO_CONTENT);
                response.setError("none");
                response.setMessage("Task deleted successfully");
                taskService.deleteTask(taskId);
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
    @PutMapping(path = "edit/private/{taskId}")
    @PreAuthorize("hasAuthority('task:private-edit')")
    public ResponseEntity<Object> editPrivateTask(@RequestBody Task task, @PathVariable("taskId") String taskId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.taskService.exists(taskId)) {

            Task originalTask = this.taskService.getTask(taskId);

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
            this.taskService.editTask(task, taskId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task does not exists");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "status/private/{taskId}/{modifiedById}")
    @PreAuthorize("hasAuthority('task:private-change-status')")
    public ResponseEntity<Object> changePrivateTaskStatus(@PathVariable("taskId") String taskId, @PathVariable("modifiedById") String modifiedById, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.taskService.exists(taskId)) {

            Task task = this.taskService.getTask(taskId);

            String authorId = task.getAuthorId();
            String responsibleId = task.getResponsibleId();

            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);
            AuthorVerifier responsibleVerifier = new AuthorVerifier(request, secretKey, responsibleId);
            if (authorVerifier.isAuthor()) {
                task.setLastUserId(authorId);
            } else if (responsibleVerifier.isAuthor()) {
                task.setLastUserId(responsibleId);
            } else {
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setError("not authorized");
                response.setMessage("You are not authorized to change the status of this task");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("Task status changed successfully");
            this.taskService.changeTaskStatus(taskId, modifiedById);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("Task with id " + taskId + " does not exists!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
