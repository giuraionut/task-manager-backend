package com.example.api.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(@Qualifier("team") TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    //------------------------------------------------------------------------------------------------------------------
    public boolean nameExists(String name) {
        return this.teamRepository.findByName(name).isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------
    public boolean exists(String teamId) {
        return this.teamRepository.findById(teamId).isPresent();
    }

    //------------------------------------------------------------------------------------------------------------------
    public Team createTeam(Team team, String leaderId) {
        team.setAuthorId(leaderId);
        team.setMembersId(new HashSet<>());
        return this.teamRepository.insert(team);
    }

    //------------------------------------------------------------------------------------------------------------------
    public Team getTeam(String teamId) {
        return this.teamRepository.findById(teamId).orElseThrow(() -> new IllegalStateException("Team with id " + teamId + " does not exists!"));
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void deleteTeam(String teamId) {
        this.teamRepository.deleteById(teamId);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void addMembers(String teamId, String userId) {
        Team team = getTeam(teamId);
        Set<String> membersId = team.getMembersId();
        if (membersId.isEmpty()) {
            membersId = new HashSet<>();
        }
        membersId.add(userId);
        team.setMembersId(membersId);
        this.teamRepository.save(team);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void removeMembers(String teamId, String userId) {
        Team team = getTeam(teamId);
        Set<String> membersId = team.getMembersId();
        membersId.remove(userId);
        team.setMembersId(membersId);
        this.teamRepository.save(team);
    }
    //------------------------------------------------------------------------------------------------------------------

    public Set<String> getMembers(String teamId) {
        Team team = getTeam(teamId);
        return team.getMembersId();
    }
}
