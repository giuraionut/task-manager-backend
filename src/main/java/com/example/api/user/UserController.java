package com.example.api.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(path = "register")
    public void register (@RequestBody User newUser)
    {
            userService.userRegister(newUser);
    }

}
