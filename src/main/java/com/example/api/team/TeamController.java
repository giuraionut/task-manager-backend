package com.example.api.team;

import com.example.api.response.Response;
import com.example.api.security.UserRole;
import com.example.api.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/team")
@AllArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    @PostMapping(path = "new/{leaderId}")
    @PreAuthorize("hasAuthority('team:create')")
    public ResponseEntity<Object> createTeam(@RequestBody Team team, @PathVariable("leaderId") String leaderId) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        if (this.teamService.nameExists(team.getTeamName())) {
            response.setMessage("Team with name " + "'" + team.getTeamName() + "'" + " already exists");
            response.setError("duplicate found");
        } else {
            response.setMessage("Team with name " + "'" + team.getTeamName() + "'" + " created successfully");
            response.setError("none");

            this.userService.setGrantedAuthorities(leaderId, UserRole.LEADER.getGrantedAuthorities());
            String teamId = this.teamService.createTeam(team, leaderId).getId();
            this.userService.setTeamId(leaderId, teamId);

        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "delete/{teamId}")
    @PreAuthorize("hasAuthority('team:delete')")
    public ResponseEntity<Object> deleteTeam(String teamId)
    {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if(this.teamService.exists(teamId))
        {
            this.teamService.deleteTeam(teamId);
            response.setMessage("Team delete successfully");
            response.setStatus(HttpStatus.NO_CONTENT);
            response.setError("none");
            return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
        }
        else
        {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage("Team with id " + teamId + " does not exists!");
            response.setError("not found");
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "add/{teamId}/{userId}")
    @PreAuthorize("hasAuthority('team:invite')")
    public ResponseEntity<Object> addUser(@PathVariable("teamId") String teamId, @PathVariable("userId") String userId) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.userService.exists(userId) && this.teamService.exists(teamId)) {
            this.userService.setTeamId(userId, teamId);
            this.teamService.addMembers(teamId, userId);
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("User added successfully");
            this.userService.setGrantedAuthorities(userId,UserRole.MEMBER.getGrantedAuthorities());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("not found");
            response.setMessage("User with id " + userId + " or team with id " + teamId + " does not exists!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "kick/{teamId}/{userId}")
    @PreAuthorize("hasAuthority('team:kick')")
    public ResponseEntity<Object> kickUser(@PathVariable("teamId") String teamId, @PathVariable("userId") String userId) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        if (this.userService.exists(userId) && this.teamService.exists(teamId)) {
            this.userService.deleteTeam(userId);
            this.teamService.removeMembers(teamId, userId);
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setMessage("User kicked successfully");
            this.userService.setGrantedAuthorities(userId,UserRole.USER.getGrantedAuthorities());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage("User with id " + userId + " or team with id " + teamId + " does not exists!");
            response.setError("not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
