package com.canu.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessageBean {
    private String name;
    private String message;

    @NotNull
    private Long fromUser;

    @NotNull
    private Long toUser;

}
