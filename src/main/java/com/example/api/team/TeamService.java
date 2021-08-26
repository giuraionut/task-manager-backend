package com.example.api.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
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
        if(teamId != null) {
            return this.teamRepository.findById(teamId).isPresent();
        }
        else{
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public Team createTeam(Team team, String leaderId) {
        team.setAuthorId(leaderId);
        Set<String> membersId = new HashSet<>();
        membersId.add(leaderId);
        team.setMembersId(membersId);
        return this.teamRepository.insert(team);
    }

    //------------------------------------------------------------------------------------------------------------------
    public Team getTeam(String teamId) {
        Optional<Team> teamById = this.teamRepository.findById(teamId);
        return teamById.orElse(null);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void deleteTeam(String teamId) {
        this.teamRepository.deleteById(teamId);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void addMembers(Team team, String userId) {
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
    public void removeMembers(Team team, String userId) {
        Set<String> membersId = team.getMembersId();
        membersId.remove(userId);
        team.setMembersId(membersId);
        this.teamRepository.save(team);
    }

    @Transactional
    public void setAvatar(String teamId, String path)
    {
        Team team = getTeam(teamId);
        team.setAvatar(path);
        this.teamRepository.save(team);
    }

    //------------------------------------------------------------------------------------------------------------------
    public Team getTeamByAuthor(String authorId)
    {
        Optional<Team> teamByAuthorId = this.teamRepository.findByAuthorId(authorId);
        return teamByAuthorId.orElse(null);
    }
}
