package com.canu.dto.requests;

import com.common.dtos.validation.ExtendedEmailValidator;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    @ExtendedEmailValidator(message = "username is not email format.")
    private String email;

    @NotBlank
    private String password;
}
