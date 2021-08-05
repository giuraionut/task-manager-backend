package com.example.api.response;

import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
public class LoginResponse {
    private HttpStatus status;
    private String message;
    private String error;
    private Object payload;
}
