package com.example.api.jwt;

public class UserNameAndPasswordAuthenticationRequest {
    private String userName;
    private String password;

    public UserNameAndPasswordAuthenticationRequest()
    {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
