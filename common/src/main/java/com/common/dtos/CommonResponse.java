package com.common.dtos;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class CommonResponse {
    String status;

    String message;

    String moreInfo;

    public static CommonResponse buildOkData(String message){
        return builder().status(HttpStatus.OK.getReasonPhrase()).message(message).build();
    }
}
