package com.example.api.notification;


public class TeamInvitation extends Notification {
    private String teamId;


    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamId() {
        return teamId;
    }
}
