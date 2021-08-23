package com.example.api.notification;


import com.example.api.jwt.AuthorVerifier;
import com.example.api.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "notification/api")
@AllArgsConstructor
public class NotificationController {

    private NotificationService notificationService;
    private final SecretKey secretKey;

    @PostMapping()
    public ResponseEntity<Object> post(@RequestBody TeamInvitation notification) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());
        response.setError("none");
        response.setMessage("Notification added successfully");
        response.setStatus(HttpStatus.OK);
        response.setPayload(this.notificationService.add(notification));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping()
    public ResponseEntity<Object> getAll(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);

        response.setError("none");
        response.setMessage("Notifications obtained successfully");
        response.setStatus(HttpStatus.OK);

        List<Notification> notifications = this.notificationService.getAll(authorVerifier.getRequesterId());
        response.setPayload(notifications);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------------------------------------

    @DeleteMapping()
    public ResponseEntity<Object> delete(HttpServletRequest request, @RequestBody TeamInvitation notification) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey, notification.getReceiverId());

        response.setError("none");
        response.setStatus(HttpStatus.OK);
        response.setMessage("Notification dismissed successfully");
        if (!authorVerifier.isValid()) {
            response.setMessage("You can't access other people's notifications");
        } else {
            this.notificationService.delete(notification);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Object> getOne(HttpServletRequest request) {
        Response response = new Response();
        response.setTimestamp(LocalDateTime.now());

        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);

        response.setError("none");
        response.setMessage("Notification obtained successfully");
        response.setStatus(HttpStatus.OK);

        List<Notification> notifications = this.notificationService.getAll(authorVerifier.getRequesterId());
        response.setPayload(notifications);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
