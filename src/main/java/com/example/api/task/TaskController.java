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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SecretKey secretKey;
    private final TeamService teamService;
    private final UserService userService;

    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(path = "public")
    @PreAuthorize("hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> deleteTask(@RequestBody Task task, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        if (this.taskService.exists(task.getId())) {
            String authorId = this.taskService.getTask(task.getId()).getAuthorId();
            User leader = this.userService.getUserById(authorId);
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);
            Team team = this.teamService.getTeam(leader.getTeamId());
            if (authorVerifier.isValid()) {
                response.setError("none");
                response.setMessage("Task deleted successfully");
                this.taskService.deleteTask(task);
                team.setTasksId(team.getTasksId().stream().filter(taskId -> !taskId.equals(task.getId())).collect(Collectors.toSet()));
                this.teamService.updateTeam(team);
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
    @PreAuthorize("hasAuthority('ROLE_LEADER') or hasAuthority('ROLE_MEMBER')")
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

            if (responsibleVerifier.getRequesterId().equals(task.getResponsibleId()) &&
                    (
                            !originalTask.getName().equals(task.getName()) ||
                                    !originalTask.getDetails().equals(task.getDetails())
                    )) {
                response.setError("not authorized");
                response.setMessage("You can only close or open this task");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

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
    @PreAuthorize("hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
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
    @GetMapping(path = "private")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> getPrivateTasks(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        List<Task> tasks = this.taskService.getPrivateTasks(authorVerifier.getRequesterId());

        if (!tasks.isEmpty()) {
            response.setMessage("Tasks [private] obtained successfully");
            response.setError("none");
            response.setPayload(tasks);
        } else {
            response.setMessage("No tasks found for user");
            response.setError("not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "public")
    @PreAuthorize("hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> getPublicTasks(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String requesterId = authorVerifier.getRequesterId();
        User user = this.userService.getUserById(requesterId);

        String teamId = user.getTeamId();
        if (teamId != null) {
            Team team = this.teamService.getTeam(teamId);
            Set<String> tasksId;
            if (team.getTasksId() != null) {
                tasksId = team.getTasksId();
                List<Task> tasksByTeam = this.taskService.getTasksByTeam(tasksId);
                response.setMessage("Team tasks obtained successfully");
                response.setError("none");
                response.setPayload(tasksByTeam);
            } else {
                response.setMessage("No tasks");
                response.setError("none");
            }
        } else {
            response.setMessage("User is not part of a team");
            response.setError("no team found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "private")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
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
    @PreAuthorize("hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> newPublicTask(@RequestBody Task task, @PathVariable("userId") String userId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String leaderId = authorVerifier.getRequesterId();
        Team team = this.teamService.getTeamByAuthor(leaderId);

        if (leaderId != null) {
            if (team.getMembersId().contains(userId)) {
                Task newTask = this.taskService.createTask(task, userId, leaderId);
                Set<String> tasksId;
                if (team.getTasksId() == null) {
                    tasksId = new HashSet<>();
                } else {
                    tasksId = team.getTasksId();
                }
                tasksId.add(newTask.getId());
                team.setTasksId(tasksId);
                this.teamService.updateTeam(team);
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
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
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
                response.setMessage("Can't delete other people's tasks");
            }
        } else {
            response.setError("not found");
            response.setMessage("Task does not exists");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "private")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
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
