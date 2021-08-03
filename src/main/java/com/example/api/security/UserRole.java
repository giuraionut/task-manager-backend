package com.example.api.security;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
    USER(Sets.newHashSet(
            UserPermission.TEAM_CREATE,
            UserPermission.TASK_PRIVATE_CLOSE,
            UserPermission.TASK_PRIVATE_CREATE,
            UserPermission.TASK_PRIVATE_DELETE,
            UserPermission.TASK_PRIVATE_EDIT,
            UserPermission.TASK_PRIVATE_CHANGE_STATUS
    )),
    LEADER(Sets.newHashSet(
            UserPermission.TEAM_CREATE,
            UserPermission.TEAM_DELETE,
            UserPermission.TEAM_INVITE,
            UserPermission.TEAM_KICK,
            UserPermission.TEAM_GET,
            UserPermission.TASK_CREATE,
            UserPermission.TASK_DELETE,
            UserPermission.TASK_EDIT,
            UserPermission.TASK_ASSIGN,
            UserPermission.TASK_CHANGE_STATUS,
            UserPermission.TASK_PRIVATE_CLOSE,
            UserPermission.TASK_PRIVATE_CREATE,
            UserPermission.TASK_PRIVATE_DELETE,
            UserPermission.TASK_PRIVATE_EDIT,
            UserPermission.TASK_PRIVATE_CHANGE_STATUS)),
    MEMBER(Sets.newHashSet(
            UserPermission.TASK_PRIVATE_CLOSE,
            UserPermission.TASK_PRIVATE_CREATE,
            UserPermission.TASK_PRIVATE_DELETE,
            UserPermission.TASK_PRIVATE_EDIT,
            UserPermission.TASK_PRIVATE_CHANGE_STATUS,
            UserPermission.TEAM_GET))
    ;

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return this.permissions;
    }


    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());

        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
