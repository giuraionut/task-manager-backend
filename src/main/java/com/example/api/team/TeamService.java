package com.example.api.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(@Qualifier("team") TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public boolean nameExists(String teamName) {
        return this.teamRepository.findByTeamName(teamName).isPresent();
    }

    public boolean exists(String teamId) {
        return this.teamRepository.findById(teamId).isPresent();
    }

    public Team createTeam(Team team, String leaderId) {
        team.setTeamLeaderId(leaderId);
        return this.teamRepository.insert(team);
    }

    public Team getTeam(String teamId) {
        return this.teamRepository.findById(teamId).orElseThrow(() -> new IllegalStateException("Team with id " + teamId + " does not exists!"));
    }

    public void addMembers(String teamId, String userId) {
        Team team = getTeam(teamId);
        List<String> teamMembersId = new ArrayList<>();
        teamMembersId.add(userId);
        team.setTeamMembersId(teamMembersId);
        teamRepository.save(team);
    }
}
