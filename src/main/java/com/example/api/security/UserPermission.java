package com.example.api.security;

public enum UserPermission {
    //team---------------------
    TEAM_CREATE("team:create"),
    TEAM_DELETE("team:delete"),
    TEAM_ACCEPT("team:accept"),
    TEAM_KICK("team:kick"),
    TEAM_GET("team:get"),
    //task---------------------
    TASK_CREATE("task:create"),
    TASK_DELETE("task:delete"),
    TASK_EDIT("task:edit"),
    TASK_ASSIGN("task:assign"),
    TASK_CHANGE_STATUS("task:change-status"),
    //personal tasks-----------------------------
    TASK_PRIVATE_CREATE("task:private-create"),
    TASK_PRIVATE_CLOSE("task:private-close"),
    TASK_PRIVATE_DELETE("task:private-delete"),
    TASK_PRIVATE_CHANGE_STATUS("task:private-change-status"),
    TASK_PRIVATE_EDIT("task:private-edit");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
