package com.smilegate.loginsg.ExceptionUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.FORBIDDEN);//new ErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(RuntimeException e) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.BAD_REQUEST);//new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createErrorResponse(RuntimeException e, HttpStatus httpStatus) {
        String errMsg = null;
        if (e instanceof ValidationException) {
            errMsg = e.getMessage().split("\\{")[1].split("'")[1];
            log.info(e.getMessage());
        } else {
            errMsg = e.getMessage();
        }
        return new ErrorResponse(httpStatus, errMsg);
    }
}
