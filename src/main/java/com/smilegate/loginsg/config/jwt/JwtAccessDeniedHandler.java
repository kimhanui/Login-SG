package com.smilegate.loginsg.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smilegate.loginsg.ExceptionUtil.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException, IOException {
        // 필요한 권한이 없이 접근하려 할때 403
        log.info(accessDeniedException.getMessage());
        setErrorResponse(HttpStatus.UNAUTHORIZED, response, accessDeniedException);
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(status, ex.getMessage());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errorResponse);
        } catch (IOException e) {
            log.warn("setErrorResponse occurred exception");
            e.printStackTrace();
        }
    }
}