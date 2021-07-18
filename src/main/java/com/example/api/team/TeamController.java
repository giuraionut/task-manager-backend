package com.example.api.team;


import com.example.api.user.User;
import com.example.api.user.UserController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "api/team")
public class TeamController {


    private final UserController userController = new UserController();
//    public TeamController(UserController userController) {
//        this.userController = userController;
//    }

    private final User LEADER = userController.getUser(1);

    private final List<Team> TEAMS = Arrays.asList(
            new Team(1, "teamName1", userController.getUsers().subList(1,5),LEADER),
            new Team(2, "teamName2", userController.getUsers().subList(6,9),LEADER)
    );


    public List<Team> getTeams() {
        return TEAMS;
    }

    @GetMapping(path = "{teamId}")
    @PreAuthorize("hasAuthority('team:read')")
    public Team getTeam(@PathVariable("teamId") Integer teamId)
    {
        return TEAMS.stream().filter(team -> team.getTeamId().equals(teamId)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Team with id " + teamId + " does not exists!"));
    }

    @GetMapping(path = "teamMembers/{teamId}")
    @PreAuthorize("hasAuthority('team_members:read')")
    public List<User> getTeamMembers(@PathVariable("teamId") Integer teamId)
    {
        List<Team> teams = getTeams();
        return teams.stream().filter(team -> teamId.equals(team.getTeamId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Team " + teamId +" does not exists")).getMembers();
    }


    @PostMapping(path = "new")
    @PreAuthorize("hasAuthority('team:create')")
    public void createTeam(Team team)
    {
         TEAMS.add(team);
    }

    @PostMapping(path = "delete")
    @PreAuthorize("hasAuthority('team:delete')")
    public void deleteTeam (Integer teamId)
    {
        TEAMS.removeIf(team -> team.getTeamId().equals(teamId));
    }
}
