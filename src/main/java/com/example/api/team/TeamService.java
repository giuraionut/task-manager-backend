package com.example.api.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Transactional
    public void deleteTeam(String teamId)
    {
        this.teamRepository.deleteById(teamId);
    }

    @Transactional
    public void addMembers(String teamId, String userId) {
        Team team = getTeam(teamId);
        Set<String> teamMembersId = team.getTeamMembersId();
        teamMembersId.add(userId);
        team.setTeamMembersId(teamMembersId);
        this.teamRepository.save(team);
    }

    @Transactional
    public void removeMembers(String teamId, String userId) {
        Team team = getTeam(teamId);
        Set<String> teamMembersId = team.getTeamMembersId();
        teamMembersId.remove(userId);
        team.setTeamMembersId(teamMembersId);
        this.teamRepository.save(team);
    }
}
