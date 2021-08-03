package com.example.api.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Data
@Document(collection = "user")
public class User implements UserDetails {

    @Id
    private String id;

    private String username;
    private String password;

    private String firstName;
    private String lastName;
    private String gender;

    @Indexed(unique = true)
    private String email;

    private String teamId;

    private Set<String> tasksId;
    private LocalDate birthDate;
    private String avatar;
    private Set<? extends GrantedAuthority> grantedAuthorities;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    public User(String username, String password, String firstName, String lastName, String gender, String email, LocalDate birthDate, String avatar) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.birthDate = birthDate;
        this.avatar = avatar;
    }

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


}

