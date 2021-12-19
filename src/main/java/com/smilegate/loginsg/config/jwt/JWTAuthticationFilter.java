package com.smilegate.loginsg.config.jwt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class JWTAuthticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final static String[] ACCEPTED_PATHS
            = new String[]{"/user/register", "/user/login", "/user/reissueactoken", "/user/mailpw"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isAcceptedPath(request.getRequestURI())) {
            // prefix, token
            String token = jwtProvider.resolveToken(request);
            if (token != null && jwtProvider.isTokenValid(token)) {
                log.info("isAccessTokenValid true");
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
