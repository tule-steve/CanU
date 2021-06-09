package com.canu.dto.requests;

import com.canu.model.SubStatusModel;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateSubStatusRequest {

    @NotNull(message = "Job Id id is required.")
    Long jobId;

    @NotNull(message = "Status Id id is required.")
    @Enumerated(EnumType.STRING)
    SubStatusModel.Status status;




}
