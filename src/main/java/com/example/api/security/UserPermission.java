package com.example.api.security;

public enum UserPermission {
    //team---------------------
    TEAM_CREATE("team:create"),
    TEAM_DELETE("team:delete"),
    TEAM_INVITE("team:invite"),
    TEAM_KICK("team:kick"),
    //task---------------------
    TASK_CREATE("task:create"),
    TASK_DELETE("task:delete"),
    TASK_EDIT("task:edit"),
    TASK_ASSIGN("task:assign"),
    TASK_CLOSE("task:close"),
    TASK_OPEN("task:open");


    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
