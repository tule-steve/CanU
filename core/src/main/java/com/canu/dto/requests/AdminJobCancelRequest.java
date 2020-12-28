package com.canu.dto.requests;

import lombok.Value;

@Value
public class AdminJobCancelRequest {

    Long jobId;

    Boolean refundMoney = false;

   Boolean isApproval = false;

    String note;
}
