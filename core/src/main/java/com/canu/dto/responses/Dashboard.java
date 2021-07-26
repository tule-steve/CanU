package com.canu.dto.responses;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
public class Dashboard {
    long caniCount;

    long canuCount;

    List<CanIServiceDto> canIByService;

    Map<String, Map<String, Long>> job;

}
