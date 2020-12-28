package com.codegame.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value
public class AddGiftCardRequest {

    @NotBlank(message = "item id is required")
    Long itemId;

    @NotEmpty(message = "code list is not blank")
    List<String> codes;

}
