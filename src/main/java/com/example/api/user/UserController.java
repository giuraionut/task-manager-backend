package com.example.api.user;

import com.example.api.task.Task;
import com.example.api.task.TaskController;
import com.example.api.team.Team;
import com.example.api.team.TeamController;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private static final List<User> USERS = Arrays.asList(
            new User(1, "James", Arrays.asList()),
            new User(2,"Maria", Arrays.asList()),
            new User(3,"Anna", Arrays.asList()),
            new User(4,"Mark", Arrays.asList()),
            new User(5,"Tonny", Arrays.asList()),
            new User(6,"John", Arrays.asList()),
            new User(7,"Gerald", Arrays.asList()),
            new User(8,"Steve", Arrays.asList()),
            new User(9,"Rick", Arrays.asList())
    );

    @GetMapping(path = "{userId}")
    public static User getUser(@PathVariable("userId") Integer userId)
    {
        return USERS.stream().filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User " + userId + " does not exists"));
    }

    @GetMapping(path = "teamMembers/{teamId}")
    public static List<User> getTeamMembers(@PathVariable("teamId") Integer teamId)
    {
        List<Team> teams = TeamController.getTeams();
        return teams.stream().filter(team -> teamId.equals(team.getTeamId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Team " + teamId +" does not exists")).getMembers();
    }

    @GetMapping(path = "all")
    public static List<User> getUsers() {
        return USERS;
    }

}
