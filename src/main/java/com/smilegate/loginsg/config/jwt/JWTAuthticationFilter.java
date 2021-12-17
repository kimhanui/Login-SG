package com.smilegate.loginsg.config.jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * filter every request to check validation of token
 */
@RequiredArgsConstructor
public class JWTAuthticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final static String[] ACCEPTED_PATHS = new String[]{"/user/register", "/user/login"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!isAcceptedPath(request.getRequestURI())) {
            String token = jwtProvider.resolveToken(request);
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAcceptedPath(String path) {
        for (String p : ACCEPTED_PATHS) {
            if (p.equals(path)) return true;
        }
        return false;
    }
}
