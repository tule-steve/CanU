package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CompletePaymentRequest {

    @NotNull(message = "paypalOrderId is required")
    String paypalOrderId;
}
