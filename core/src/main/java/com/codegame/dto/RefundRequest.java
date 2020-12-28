package com.codegame.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Value
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RefundRequest {

    @NotBlank(message = "Order id is required.")
    Long orderId;

    List<String> codes;

}
