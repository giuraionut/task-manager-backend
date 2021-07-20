package com.example.api.security;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
    USER(Sets.newHashSet(UserPermission.TEAM_CREATE)),
    LEADER(Sets.newHashSet(
            UserPermission.TEAM_CREATE,
            UserPermission.TEAM_DELETE,
            UserPermission.TEAM_INVITE,
            UserPermission.TEAM_KICK,
            UserPermission.TASK_CREATE,
            UserPermission.TASK_DELETE,
            UserPermission.TASK_EDIT,
            UserPermission.TASK_ASSIGN)),
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
