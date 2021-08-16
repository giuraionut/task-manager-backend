package com.example.api.chat;

import com.example.api.jwt.AuthorVerifier;
import com.example.api.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "chat/api")
@AllArgsConstructor
public class ChatController {

    private ChatService chatService;
    private final SecretKey secretKey;

    @PostMapping()
    public ResponseEntity<Object> saveChat(@RequestBody Chat chat) {
        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Message saved successfully");
        response.setError("none");
        response.setTimestamp(LocalDateTime.now());
        response.setPayload(this.chatService.saveChat(chat));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "{partnerId}")
    public ResponseEntity<Object> getChat(HttpServletRequest request, @PathVariable("partnerId") String partnerId) {
        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Messages received successfully");
        response.setError("none");
        response.setTimestamp(LocalDateTime.now());
        AuthorVerifier authorVerifier = new AuthorVerifier(request, secretKey);
        List<Chat> chats = new ArrayList<>();
        chats = this.chatService.getChat(authorVerifier.getRequesterId(), partnerId);
        response.setPayload(chats);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
