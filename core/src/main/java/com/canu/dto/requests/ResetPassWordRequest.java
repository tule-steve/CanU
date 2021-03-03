package com.canu.dto.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResetPassWordRequest {

    @NotEmpty(message = "token is not empty")
    String token;

    @NotEmpty(message = "new password id is required.")
    CharSequence newPassword;



}
