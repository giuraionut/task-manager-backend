package com.example.api.team;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.misc.MiscService;
import com.example.api.response.Response;
import com.example.api.security.UserRole;
import com.example.api.user.User;
import com.example.api.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "team")
@AllArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;
    private final MiscService miscService;
    private final SecretKey secretKey;

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping()
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> createTeam(@RequestBody Team team, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String requesterId = authorVerifier.getRequesterId();
        User user = this.userService.getUserById(requesterId);

        if (this.teamService.nameExists(team.getName())) {
            response.setMessage("Team with name " + "'" + team.getName() + "'" + " already exists");
            response.setError("duplicate found");
        }
        if (user.getTeamId() != null) {
            response.setMessage("You already have a team");
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
    @PreAuthorize("hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> deleteTeam(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        User leader = this.userService.getUserById(authorVerifier.getRequesterId());

        if (this.teamService.exists(leader.getTeamId())) {
            Team team = this.teamService.getTeam(leader.getTeamId());

            Set<String> teamMembersId = team.getMembersId();
            teamMembersId.forEach(this.userService::deleteTeam);

            this.teamService.deleteTeam(leader.getTeamId());
            this.userService.deleteTeam(leader.getId());
            response.setMessage("Team delete successfully");
            response.setError("none");
        } else {
            response.setMessage("Team with id " + leader.getTeamId() + " does not exists!");
            response.setError("not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping()
    @PreAuthorize("hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> getTeam(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        User user = this.userService.getUserById(authorVerifier.getRequesterId());

        if (this.teamService.exists(user.getTeamId())) {

            Team team = this.teamService.getTeam(user.getTeamId());
            response.setMessage("Team obtained successfully");
            response.setError("none");
            response.setPayload(team);
        } else {
            response.setMessage("Team with id " + user.getTeamId() + " does not exists!");
            response.setError("not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "add/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Object> acceptInvitation(@PathVariable("teamId") String teamId, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String requesterId = authorVerifier.getRequesterId();

        if (!this.teamService.exists(teamId)) {
            response.setError("team not found");
            response.setMessage("Team does not exists");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Team team = this.teamService.getTeam(teamId);

        this.userService.setTeamId(requesterId, team.getId());
        this.teamService.addMembers(team, requesterId);
        response.setError("none");
        response.setMessage("Invitation accepted successfully");
        this.userService.setGrantedAuthorities(requesterId, UserRole.MEMBER.getGrantedAuthorities());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "kick/{userId}")
    @PreAuthorize("hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> kickUser(@PathVariable("userId") String userId, HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String authorId = authorVerifier.getRequesterId();
        User leader = this.userService.getUserById(authorId);

        if (!this.teamService.exists(leader.getTeamId())) {
            response.setError("team not found");
            response.setMessage("Team does not exists");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Team team = this.teamService.getTeamByAuthor(authorId);

        if (this.userService.exists(userId)) {
            this.userService.deleteTeam(userId);
            this.teamService.removeMembers(team, userId);
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("User kicked successfully");
        } else {
            response.setMessage("User with id " + userId + " does not exists!");
            response.setError("not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "members")
    @PreAuthorize("hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> getMembersDetails(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        String requesterId = authorVerifier.getRequesterId();
        User requester = this.userService.getUserById(requesterId);

        String teamId = requester.getTeamId();

        if (!this.teamService.exists(requester.getTeamId())) {
            response.setError("team not found");
            response.setMessage("Team does not exists");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Team team = this.teamService.getTeam(teamId);

        if (team == null) {
            response.setError("team not found");
            response.setMessage("Team does not exists");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
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
        } else {
            response.setError("none");
            response.setMessage("No team members found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "avatar")
    @PreAuthorize("hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> uploadAvatar(@RequestParam("image") MultipartFile image, HttpServletRequest request) throws IOException {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        if (this.userService.exists(authorVerifier.getRequesterId())) {
            User user = this.userService.getUserById(authorVerifier.getRequesterId());
            String path = this.miscService.uploadImage(image, "team", user.getTeamId());
            if (!path.equals("error")) {
                this.teamService.setAvatar(user.getTeamId(), path);
                response.setError("none");
                response.setMessage("Image uploaded successfully");
                response.setPayload(path);
            } else {
                response.setError("path creation failed");
                response.setMessage("Failed to create the path for the uploaded image");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "leave")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public ResponseEntity<Object> leaveTeam(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        if (this.userService.exists(authorVerifier.getRequesterId())) {
            User user = this.userService.getUserById(authorVerifier.getRequesterId());
            Team team = this.teamService.getTeam(user.getTeamId());
            user.setTeamId(null);
            user.setGrantedAuthorities(UserRole.USER.getGrantedAuthorities());

            this.userService.updateUser(user);
            team.setMembersId(team.getMembersId().stream().filter(memberId -> !Objects.equals(memberId, user.getId())).collect(Collectors.toSet()));
            this.teamService.updateTeam(team);
            response.setError("none");
            response.setMessage("You left the team successfully");
        } else {
            response.setError("not found");
            response.setMessage("User does not exists");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
