package com.canu.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

@Value
@Builder
public class CanIDto {

    Long id;

    String avatar;

    String name;

    Long price;

    String nation;

    String address;

    BigDecimal rating;

    String areaService;

    String currency;

    String jobType;

    String caniTitle;

    Set<Long> service;


}
