package com.canu.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanIServiceDto {
    private Long serviceId;

    private String title;

    private Long count;
}
