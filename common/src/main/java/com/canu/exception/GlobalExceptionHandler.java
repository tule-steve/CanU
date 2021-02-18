package com.canu.exception;

import com.common.dtos.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    final private String errorMessage = "We have encountered an unexpected condition which prevented us from fulfilling the request. Sorry for the inconvenience caused.";

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        logger.error("error: ", ex);
        return new ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleBadCredential(BadCredentialsException ex) {
        GlobalValidationException exception = new GlobalValidationException(ex.getMessage());
        logger.error("error: ", ex);
        return new ResponseEntity(CommonResponse.buildBadRequestData(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    //Handle for case using entity for DTO in controller
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        StringBuilder sb = new StringBuilder();
        ex.getConstraintViolations().forEach(e -> {
            sb.append(e.getPropertyPath());
            sb.append(" ");
            sb.append(e.getMessage());
            sb.append(System.lineSeparator());
        });
        GlobalValidationException exception = new GlobalValidationException(ex.getMessage());
        logger.error("error: ", ex);
        return new ResponseEntity(sb.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GlobalValidationException.class)
    public final ResponseEntity<Object> handleApplicationExceptions(GlobalValidationException ex) {
        return new ResponseEntity(CommonResponse.buildBadRequestData(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return new ResponseEntity(details, HttpStatus.BAD_REQUEST);
    }

}