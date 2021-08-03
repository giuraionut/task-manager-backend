package com.example.api.team;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.response.Response;
import com.example.api.security.UserRole;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "team")
@AllArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;
    private final SecretKey secretKey;

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping()
    @PreAuthorize("hasAuthority('team:create')")
    public ResponseEntity<Object> createTeam(@RequestBody Team team, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String requesterId = authorVerifier.getRequesterId();
        if (this.teamService.nameExists(team.getName())) {
            response.setMessage("Team with name " + "'" + team.getName() + "'" + " already exists");
            response.setError("duplicate found");
        } else {
            response.setMessage("Team with name " + "'" + team.getName() + "'" + " created successfully");
            response.setError("none");

            this.userService.setGrantedAuthorities(requesterId, UserRole.LEADER.getGrantedAuthorities());
            String teamId = this.teamService.createTeam(team, requesterId).getId();
            this.userService.setTeamId(requesterId, teamId);

        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping()
    @PreAuthorize("hasAuthority('team:delete')")
    public ResponseEntity<Object> deleteTeam(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        User leader = this.userService.getUserById(authorVerifier.getRequesterId());
        if (this.teamService.exists(leader.getTeamId())) {
            this.teamService.deleteTeam(leader.getTeamId());
            response.setMessage("Team delete successfully");
            response.setStatus(HttpStatus.NO_CONTENT);
            response.setError("none");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage("Team with id " + leader.getTeamId() + " does not exists!");
            response.setError("not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping()
    @PreAuthorize("hasAuthority('team:get')")
    public ResponseEntity<Object> getTeam(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        User user = this.userService.getUserById(authorVerifier.getRequesterId());
        if (this.teamService.exists(user.getTeamId())) {
            Team team = this.teamService.getTeam(user.getTeamId());
            response.setMessage("Team obtained successfully");
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setPayload(team);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage("Team with id " + user.getTeamId() + " does not exists!");
            response.setError("not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "invite/{teamId}/{userId}")
    @PreAuthorize("hasAuthority('team:invite')")
    public ResponseEntity<Object> inviteMember(@PathVariable("teamId") String teamId, @PathVariable("userId") String userId, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.userService.exists(userId) && this.teamService.exists(teamId)) {

            String authorId = this.teamService.getTeam(teamId).getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isAuthor()) {
                this.userService.setTeamId(userId, teamId);
                this.teamService.addMembers(teamId, userId);
                response.setStatus(HttpStatus.OK);
                response.setError("none");
                response.setMessage("User added successfully");
                this.userService.setGrantedAuthorities(userId, UserRole.MEMBER.getGrantedAuthorities());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setError("not authorized");
                response.setMessage("Can't assign users in other teams");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("User with id " + userId + " or team with id " + teamId + " does not exists!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "kick/{teamId}/{userId}")
    @PreAuthorize("hasAuthority('team:kick')")
    public ResponseEntity<Object> kickUser(@PathVariable("teamId") String teamId, @PathVariable("userId") String userId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.userService.exists(userId) && this.teamService.exists(teamId)) {

            String authorId = this.teamService.getTeam(teamId).getAuthorId();
            AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, authorId);

            if (authorVerifier.isAuthor()) {
                this.userService.deleteTeam(userId);
                this.teamService.removeMembers(teamId, userId);
                response.setStatus(HttpStatus.OK);
                response.setError("none");
                response.setMessage("User kicked successfully");
                this.userService.setGrantedAuthorities(userId, UserRole.USER.getGrantedAuthorities());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setError("not authorized");
                response.setMessage("Can't kick users from other teams");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage("User with id " + userId + " or team with id " + teamId + " does not exists!");
            response.setError("not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "teamMembers")
    @PreAuthorize("hasAuthority('team:get')")
    public ResponseEntity<Object> getMembersDetails(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String requesterId = authorVerifier.getRequesterId();
        User requester = this.userService.getUserById(requesterId);
        String teamId = requester.getTeamId();
        Team team = this.teamService.getTeam(teamId);
        Set<String> membersId = team.getMembersId();
        if (!membersId.isEmpty()) {
            List<User> members = new ArrayList<>();
            membersId.forEach(memberId ->
            {
                User user = this.userService.getUserById(memberId);
                user.setPassword(null);
                members.add(user);
            });
            response.setPayload(members);
            response.setError("none");
            response.setMessage("Team members obtained successfully");
            response.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setError("none");
            response.setMessage("No team members found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }
}
