package com.example.api.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.Data;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Data
public class AuthorVerifier {


    private HttpServletRequest request;
    private String requesterId;
    private String authorId;
    private SecretKey secretKey;
    private boolean isValid;


    public AuthorVerifier(HttpServletRequest request, SecretKey secretKey) {

        Cookie[] cookies = request.getCookies();

        if (cookies.length == 0) {
            this.requesterId = null;
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName()
                        .equals("jwtToken"))
                .findFirst().map(Cookie::getValue).orElse(null);

        if (token == null) {
            this.requesterId = null;
        }

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();

        this.requesterId = (String) body.get("userId");
    }

    public AuthorVerifier(HttpServletRequest request, SecretKey secretKey, String userId) {

        Cookie[] cookies = request.getCookies();

        if (cookies.length == 0) {
            this.isValid = false;
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName()
                        .equals("jwtToken"))
                .findFirst().map(Cookie::getValue).orElse(null);

        if (token == null) {
            this.isValid = false;
        }

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();


        this.requesterId = (String) body.get("userId");

        if (requesterId == null) {
            this.isValid = false;
        } else {
            this.isValid = userId.equals(this.requesterId);
        }
    }
}
