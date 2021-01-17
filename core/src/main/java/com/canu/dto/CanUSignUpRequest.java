package com.canu.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CanUSignUpRequest {

    @NotEmpty(message = "email id is required.")
    String email;

    @NotEmpty(message = "password id is required.")
    CharSequence password;



}
