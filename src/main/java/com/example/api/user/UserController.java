package com.example.api.user;

import com.example.api.team.TeamController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private TeamController teamController;

    private static final List<User> USERS = Arrays.asList(
            new User(1, "James", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(2,"Maria", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(3,"Anna", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(4,"Mark", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(5,"Tonny", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(6,"John", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(7,"Gerald", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(8,"Steve", Arrays.asList(), "email", "birthDate", "avatar"),
            new User(9,"Rick", Arrays.asList(), "email", "birthDate", "avatar")
    );

    @GetMapping(path = "{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public User getUser(@PathVariable("userId") Integer userId)
    {
        return USERS.stream().filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User " + userId + " does not exists"));
    }

    @GetMapping(path = "all")
    public List<User> getUsers() {
        return USERS;
    }

}
