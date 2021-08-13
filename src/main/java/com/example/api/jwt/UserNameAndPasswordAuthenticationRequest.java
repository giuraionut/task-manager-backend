package com.example.api.jwt;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserNameAndPasswordAuthenticationRequest {
    @NotBlank
    private String username;
    private String password;
}
