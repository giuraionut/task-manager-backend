package com.example.api.jwt;

import lombok.Data;

@Data
public class UserNameAndPasswordAuthenticationRequest {
    private String username;
    private String password;

}
