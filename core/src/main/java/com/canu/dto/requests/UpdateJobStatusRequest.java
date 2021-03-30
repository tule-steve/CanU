package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateJobStatusRequest {

    @NotNull(message = "Requested User Id is required")
    Long requestedUserId;

    @NotNull(message = "Job Id is required")
    Long jobId;

    @NotNull(message = "price is required")
    Long price;
}
