package com.example.api.security;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
    MEMBER(Sets.newHashSet(UserPermission.TASK_READ)),
    LEADER(Sets.newHashSet(
            UserPermission.TEAM_CREATE,
            UserPermission.TEAM_DELETE,
            UserPermission.USER_INVITE,
            UserPermission.USER_KICK,
            UserPermission.TASK_ASSIGN,
            UserPermission.USER_READ,
            UserPermission.TASK_READ,
            UserPermission.TEAM_MEMBER_READ,
            UserPermission.TEAM_READ,
            UserPermission.TASK_CREATE
            )),
    TEMP_LEADER(Sets.newHashSet(UserPermission.TASK_ASSIGN,
            UserPermission.TEAM_MEMBER_READ,
            UserPermission.TASK_READ));

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions(){
        return this.permissions;
    }


    public Set<SimpleGrantedAuthority> getGrantedAuthorities()
    {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());

        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
