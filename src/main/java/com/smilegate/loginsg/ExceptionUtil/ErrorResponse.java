package com.smilegate.loginsg.ExceptionUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private HttpStatus status;
    private String msg;
}
