package com.example.api.jwt;

public class UserNameAndPasswordAuthenticationRequest {
    private String username;
    private String password;

    public UserNameAndPasswordAuthenticationRequest()
    {
    }

    public String getUsername() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
