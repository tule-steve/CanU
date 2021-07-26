package com.canu.dto.requests;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class AdminJobCancelRequest {

    @NotNull(message = "Job Id id is required.")
    Long jobId;

    Boolean refundMoney = false;

    @NotNull(message = "Admin response is required.")
    Boolean isApproval = false;

    String note;
}
