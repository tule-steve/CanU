package com.canu.dto.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChangePassWordRequest {

    @NotEmpty(message = "old password id is required.")
    CharSequence oldPassword;

    @NotEmpty(message = "new password id is required.")
    CharSequence newPassword;



}
