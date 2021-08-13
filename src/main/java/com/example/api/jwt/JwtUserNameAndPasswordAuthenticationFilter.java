package com.example.api.jwt;

import com.example.api.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtUserNameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final Gson gson = new Gson();

    public JwtUserNameAndPasswordAuthenticationFilter(
            AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {

        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            UserNameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().
                    readValue(request.getInputStream(), UserNameAndPasswordAuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );
            return authenticationManager.authenticate(authentication);


        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException {

        Cookie jwtToken = new Cookie("jwtToken", jwtTokenUtils.generateToken(authentication));
        jwtToken.setSecure(false);
        jwtToken.setDomain("localhost");
        jwtToken.setPath("/");
        jwtToken.setHttpOnly(true);
        jwtToken.setMaxAge(86400);
        LoginResponse res = new LoginResponse();
        res.setStatus(HttpStatus.OK);
        res.setMessage("Authentication successfully");
        res.setPayload(null);
        res.setError("none");

        String gsonRes = this.gson.toJson(res);
        response.addCookie(jwtToken);
        response.getWriter().print(gsonRes);
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        LoginResponse res = new LoginResponse();
        res.setStatus(HttpStatus.BAD_REQUEST);
        res.setMessage("Authentication failed");
        res.setError("wrong credentials");
        String gsonRes = this.gson.toJson(res);
        response.setStatus(200);
        response.getWriter().print(gsonRes);
        response.getWriter().flush();
    }
}
