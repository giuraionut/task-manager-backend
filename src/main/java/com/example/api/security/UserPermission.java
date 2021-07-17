package com.example.api.security;

public enum UserPermission {
    TEAM_CREATE("team:create"),
    TEAM_DELETE("team:delete"),
    USER_INVITE("user:invite"),
    USER_KICK("user:kick"),
    USER_GET("user:get"),
    TASK_ASSIGN("task:assign");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
    }
