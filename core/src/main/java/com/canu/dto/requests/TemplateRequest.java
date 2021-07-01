package com.canu.dto.requests;

import com.canu.model.NotificationDetailModel;
import lombok.Value;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Value
public class TemplateRequest {

    @NotNull(message = "Template detail is required")
    String template;

    @NotNull(message = "title detail is required")
    String title;

    @NotNull(message = "description detail is required")
    String description;

    @NotNull(message = "type detail is required")
    @Enumerated(EnumType.STRING)
    NotificationDetailModel.Type type;
}
