package com.canu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class File64Dto {
    String fileName;

    String encrypt64File;

    String fileType;

    @NotNull
    private Long fromUser;

    @NotNull
    private Long toUser;
}
