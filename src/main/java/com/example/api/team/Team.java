package com.example.api.team;

import com.example.api.user.User;

import java.util.List;

public class Team {
    private final Integer teamId;
    private final String teamName;
    private final List<User> members;
    private final User leader;

    public Team(Integer teamId, String teamName, List<User> members, User leader) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.members = members;
        this.leader = leader;
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public List<User> getMembers() {
        return members;
    }

    public User getLeader() {
        return leader;
    }
}
