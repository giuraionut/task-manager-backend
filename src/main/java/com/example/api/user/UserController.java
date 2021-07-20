package com.example.api.user;

import com.example.api.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping(path = "")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(path = "register")
    public ResponseEntity<Object> register(@RequestBody User newUser) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        if (this.userService.emailExists(newUser) || this.userService.usernameExists(newUser)) {
            response.setMessage("Email or username already exists");
            response.setError("duplicate found");
        } else {
            response.setMessage("Registration successfully");
            response.setError("none");
            this.userService.register(newUser);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(path = "user/newName/{userId}")
    public ResponseEntity<Object> changeName(@RequestBody User user, @PathVariable("userId") String userId) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Name changed successfully");
        response.setError("none");
        this.userService.changeName(user, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
