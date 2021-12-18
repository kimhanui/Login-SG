package com.smilegate.loginsg.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smilegate.loginsg.ExceptionUtil.ErrorResponse;
import com.smilegate.loginsg.ExceptionUtil.JwtValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomFilterExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("filter handler");
        try {
            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
        } catch (NullPointerException e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
        } catch (JwtValidationException e) {
            e.printStackTrace();
            setErrorResponse(e.getStatus(), response, e);
        } catch (Throwable e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e);
        }
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
