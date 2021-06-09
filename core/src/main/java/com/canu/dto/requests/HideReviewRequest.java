package com.canu.dto.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HideReviewRequest {

    @NotNull(message = "Job Id id is required.")
    Long jobId;

    @NotNull(message = "isCanI flag id is required.")
    Boolean isCanI;




}
