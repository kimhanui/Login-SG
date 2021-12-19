package com.smilegate.loginsg.ExceptionUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        e.printStackTrace();
        ErrorResponse response = createErrorResponse(e, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(RuntimeException e) {
        e.printStackTrace();
        ErrorResponse response = createErrorResponse(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        ErrorResponse response = createErrorResponse(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({JwtValidationException.class})
    public ResponseEntity<ErrorResponse> handleJwtValidationException(JwtValidationException e) {
        ErrorResponse response = createErrorResponse(e, e.getStatus());
        return new ResponseEntity<>(response, e.getStatus());
    }

    @ExceptionHandler({MailSendException.class, MailAuthenticationException.class, MailParseException.class})
    public ResponseEntity<ErrorResponse> handleJwtValidationException(RuntimeException e) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

    private ErrorResponse createErrorResponse(Errors e, HttpStatus httpStatus) {
        String errMsg = null;
        if (e instanceof MethodArgumentNotValidException) {
            errMsg = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else {
            errMsg = e.getAllErrors().toString();
        }
        return new ErrorResponse(httpStatus, errMsg);
    }
}
