package com.example.api.team;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "team")
public class Team {

    @Id
    private String id;

    @Indexed(unique = true)
    private String teamName;

    @Indexed(unique = true)
    private String teamLeaderId;
    private Set<String> teamMembersId;

}
