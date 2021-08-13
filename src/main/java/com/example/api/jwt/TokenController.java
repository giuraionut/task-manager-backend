package com.example.api.jwt;

import com.example.api.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "token")
@AllArgsConstructor
public class TokenController {

    private JwtTokenUtils jwtTokenUtils;

    @PostMapping(path = "refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshToken refreshToken, HttpServletResponse HttpResponse) {
        Response response = new Response();
        response.setMessage("Token refreshed successfully");
        response.setTimestamp(LocalDateTime.now());
        response.setError("none");
        response.setStatus(HttpStatus.OK);
        String token = jwtTokenUtils.refreshToken(refreshToken.getRefreshToken());
        System.out.println(token);
        Cookie newJwtToken = new Cookie("jwtToken", token);
        newJwtToken.setSecure(false);
        newJwtToken.setDomain("localhost");
        newJwtToken.setPath("/");
        newJwtToken.setHttpOnly(true);
        newJwtToken.setMaxAge(86400);


        HttpResponse.addCookie(newJwtToken);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
