package com.canu.dto.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CanIDto {

    Long id;

    String avatar;

    String name;

    Long price;

    String nation;

    String address;
}
