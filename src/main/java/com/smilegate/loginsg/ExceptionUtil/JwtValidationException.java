package com.smilegate.loginsg.ExceptionUtil;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JwtValidationException extends RuntimeException {

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public JwtValidationException() {
        super();
    }

    public JwtValidationException(String message) {
        super(message);
    }

    public JwtValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public JwtValidationException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
