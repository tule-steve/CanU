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

    Object data;

    public static CommonResponse buildOkData(String message){
        return builder().status(HttpStatus.OK.getReasonPhrase()).message(message).build();
    }

    public static CommonResponse buildOkData(String message, Object data){
        return builder().status(HttpStatus.OK.getReasonPhrase()).message(message).data(data).build();
    }



    public static CommonResponse buildBadRequestData(String message){
        return builder().status(HttpStatus.BAD_REQUEST.getReasonPhrase()).message(message).build();
    }

    public static CommonResponse buildInternalErrorRequestData(String message){
        return builder().status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).message(message).build();
    }
}
