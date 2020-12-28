package com.codegame.security.DTOs;

import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
public class LoginResponse {
    HttpStatus status;
    String email;
    String message;
}
