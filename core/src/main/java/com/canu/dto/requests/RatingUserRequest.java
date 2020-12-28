package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingUserRequest {

    @NotNull(message = "jobId is required")
    Long jobId;

    @NotNull(message = "value is required")
    Integer value;

    String content;
}
