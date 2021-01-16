package com.canu.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class GlobalValidationException extends RuntimeException {
    private String message;
}
