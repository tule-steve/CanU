package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TransactionRequest {

    @NotNull(message = "jobId is required")
    Long jobId;

    String couponCode;
}
