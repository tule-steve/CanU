package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectCouponRequest {

    @NotNull(message = "couponCode is required")
    String couponCode;
}
