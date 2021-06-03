package com.canu.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemRatingDto {
    private final Long userID;
    private final String userName;
    private final String criteria;
    private final Integer rating;

}
