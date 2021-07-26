package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LockUserRequest {

    @NotNull(message = "user id is required")
    Long userId;

    @NotNull(message = "isLocked is required")
    Boolean isLocked;
}
