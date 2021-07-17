package com.example.api.team;


import com.example.api.user.User;
import com.example.api.user.UserController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "api/team")
public class TeamController {





    private static final User LEADER = UserController.getUser(1);

    private static final List<Team> TEAMS = Arrays.asList(
            new Team(1,UserController.getUsers().subList(1,5),LEADER),
            new Team(2,UserController.getUsers().subList(6,9),LEADER)
    );

    public static List<Team> getTeams() {
        return TEAMS;
    }

    @PostMapping(path = "new")
    public void createTeam(Team team)
    {
         TEAMS.add(team);
    }
}
