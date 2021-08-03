package com.example.api.user;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;



@RestController
@RequestMapping(path = "user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecretKey secretKey;

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "new")
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
            this.userService.add(newUser);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "signout")
    public ResponseEntity<Object> logout(HttpServletResponse HttpResponse) {


        Cookie jwtToken = new Cookie("jwtToken", null);
        jwtToken.setSecure(false);
        jwtToken.setDomain("localhost");
        jwtToken.setPath("/");
        jwtToken.setHttpOnly(true);
        jwtToken.setMaxAge(0);

        HttpResponse.addCookie(jwtToken);

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);
        response.setError("none");
        response.setMessage("Signed out successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(path = "name")
    public ResponseEntity<Object> changeName(@RequestBody User user, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        if (this.userService.exists(authorVerifier.getRequesterId())) {
                response.setStatus(HttpStatus.OK);
                response.setMessage("Name changed successfully");
                response.setError("none");
                this.userService.changeName(user, authorVerifier.getRequesterId());
                return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("User does not exists");
            response.setError("not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "profile")
    public ResponseEntity<Object> getProfile(HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        if (this.userService.exists(authorVerifier.getRequesterId())) {
            User user = this.userService.getUserById(authorVerifier.getRequesterId());
            user.setPassword(null);
            response.setMessage("User found");
            response.setStatus(HttpStatus.OK);
            response.setError("none");
            response.setPayload(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("User does not exists");
            response.setError("not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
