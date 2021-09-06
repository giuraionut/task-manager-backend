package com.example.api.user;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.misc.MiscService;
import com.example.api.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;


@RestController
@RequestMapping(path = "user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final MiscService miscService;
    private final SecretKey secretKey;

    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(path = "new")
    public ResponseEntity<Object> register(@RequestBody User newUser) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        String checkUser = this.userService.checkUserReg(newUser);
        if (!checkUser.equals("ok")) {
            response.setMessage(checkUser);
            response.setError("invalid user");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
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
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
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
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> changeName(@RequestBody User user, HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        if (this.userService.exists(authorVerifier.getRequesterId())) {
            response.setMessage("Name changed successfully");
            response.setError("none");
            this.userService.changeName(user, authorVerifier.getRequesterId());
        } else {
            response.setMessage("User does not exists");
            response.setError("not found");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "profile")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> getProfile(HttpServletRequest request) {

        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        if (this.userService.exists(authorVerifier.getRequesterId())) {
            User user = this.userService.getUserById(authorVerifier.getRequesterId());
            user.setPassword(null);
            response.setMessage("Profile found");
            response.setError("none");
            response.setPayload(user);
        } else {
            response.setMessage("Profile does not exists");
            response.setError("not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_LEADER') or hasAuthority('ROLE_MEMBER')")
    public ResponseEntity<Object> getUserInfo(@PathVariable("userId") String userId) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);
        if (this.userService.exists(userId)) {
            User user = this.userService.getUserById(userId);
            user.setPassword(null);
            response.setMessage("User found");
            response.setError("none");
            response.setPayload(user);
        } else {
            response.setMessage("User does not exists");
            response.setError("not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "avatar")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> uploadAvatar(@RequestParam("image") MultipartFile image, HttpServletRequest request) throws IOException {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        if (this.userService.exists(authorVerifier.getRequesterId())) {
            String path = this.miscService.uploadImage(image, "user", authorVerifier.getRequesterId());
            if (!path.equals("error")) {
                this.userService.setAvatar(authorVerifier.getRequesterId(), path);
                response.setError("none");
                response.setMessage("Image uploaded successfully");
                response.setPayload(path);
            } else {
                response.setError("path creation failed");
                response.setMessage("Failed to create the path for the uploaded image");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "update")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_MEMBER') or hasAuthority('ROLE_LEADER')")
    public ResponseEntity<Object> updateUser(@RequestBody User user, HttpServletRequest request)
    {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.OK);

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);

        if(this.userService.exists(authorVerifier.getRequesterId()))
        {
            User requester = this.userService.getUserById(authorVerifier.getRequesterId());
            requester.setUsername(user.getUsername());
            requester.setPassword(user.getPassword());
            requester.setEmail(user.getEmail());
            requester.setAboutMe(user.getAboutMe());
            response.setError("none");
            response.setMessage("User updated successfully");
            this.userService.updateUser(requester);
        }
        else
        {
            response.setError("not found");
            response.setMessage("Failed to update user because no user found");
        }

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
