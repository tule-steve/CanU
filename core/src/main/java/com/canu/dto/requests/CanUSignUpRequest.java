package com.canu.dto.requests;

import com.common.dtos.validation.ExtendedEmailValidator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CanUSignUpRequest {

    @NotEmpty(message = "email id is required.")
    @ExtendedEmailValidator(message = "username is not email format.")
    String email;

    @NotEmpty(message = "password id is required.")
    CharSequence password;

    String firstName;

    String lastName;

}
