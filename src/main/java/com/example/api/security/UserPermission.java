package com.example.api.security;

public enum UserPermission {
    TEAM_CREATE("team:create"),
    TEAM_DELETE("team:delete"),
    TEAM_MEMBER_READ("team_member:read"),
    USER_INVITE("user:invite"),
    USER_KICK("user:kick"),
    USER_READ("user:read"),
    TASK_READ("task:read"),
    TEAM_READ("team:read"),
    TASK_ASSIGN("task:assign"),
    TASK_CREATE("task:create");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
    }
