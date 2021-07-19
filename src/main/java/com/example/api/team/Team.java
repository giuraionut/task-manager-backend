package com.example.api.team;


import com.example.api.user.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "team")
public class Team {

    @Id
    private String id;

    private String teamName;
    private User teamLeader;
    private List<User> teamMembers;

    public Team(String teamName, User teamLeader, List<User> teamMembers) {
        this.teamName = teamName;
        this.teamLeader = teamLeader;
        this.teamMembers = teamMembers;
    }

}
