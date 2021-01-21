package com.canu.dto.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SocialSignUpRequest {

    @NotEmpty(message = "email id is required.")
    String email;

    @NotEmpty(message = "provider type is required.")
    String providerType;

    @NotEmpty(message = "provider key is required.")
    String providerKey;



}
