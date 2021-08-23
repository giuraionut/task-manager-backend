package com.example.api.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public enum UserRole {
    USER(),
    LEADER(),
    MEMBER();

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> role = new HashSet<>();

        role.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return role;
    }
}
