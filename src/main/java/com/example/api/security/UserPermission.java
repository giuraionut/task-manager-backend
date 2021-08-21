package com.example.api.security;

public enum UserPermission {
    //team---------------------
    TEAM_CREATE("team:create"),
    TEAM_DELETE("team:delete"),
    TEAM_ACCEPT("team:accept"),
    TEAM_KICK("team:kick"),
    TEAM_GET("team:get"),
    //task public---------------------
    TASK_PUBLIC_CREATE("task:public-create"),
    TASK_PUBLIC_DELETE("task:public-delete"),
    TASK_PUBLIC_EDIT("task:public-edit"),
    TASK_PUBLIC_GET("task:public-get"),
    TASK_COUNT("task:count"),
    //personal tasks-----------------------------
    TASK_PRIVATE_CREATE("task:private-create"),
    TASK_PRIVATE_DELETE("task:private-delete"),
    TASK_PRIVATE_EDIT("task:private-edit"),
    TASK_PRIVATE_GET("task:private-get");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
