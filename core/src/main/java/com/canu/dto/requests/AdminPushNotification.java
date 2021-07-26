package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminPushNotification {

    @NotBlank(message = "title is required")
    String title;

    @NotNull(message = "description is required")
    String description;

    @NotNull(message = "email data is required")
    String emailBody;

    Long userId;

    Boolean isAll = false;
}
