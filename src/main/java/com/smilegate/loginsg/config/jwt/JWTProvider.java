package com.smilegate.loginsg.config.jwt;

import com.smilegate.loginsg.ExceptionUtil.JwtValidationException;
import com.smilegate.loginsg.domain.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JWTProvider {

    private final UserDetailsService userDetailsService;
    private static final String JWT_PREFIX = "Bearer";
//    private static final String REFRESH_PREFIX = "Refresh";
    private final String SECRET_KEY;
    private final long ACCESS_VALID_TIME;
    private final long REFRESH_VALID_TIME;

    @Autowired
    public JWTProvider(@Value("${jwt.secret-key}") String secretKey,
                       @Value("${jwt.access-valid-time}") long accessValidTime,
                       @Value("${jwt.refresh-valid-time}") long refreshValidTime,
                       UserDetailsService userDetailsService) {
        this.SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.ACCESS_VALID_TIME = accessValidTime;
        this.REFRESH_VALID_TIME = refreshValidTime;
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                //payload
                .setSubject("userAccessToken") // token 제목
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_VALID_TIME))
                //signature
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // accessToken과 다르게 만드려면 넣는 값도 달라야함
    public String createRefreshToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                //payload
                .setSubject("userRefreshToken") // token 제목
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_VALID_TIME))
                //signature
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * JWT -> Authentication for Spring Boot
     * - credentials: 로그인 인증 후엔 값을 제거해서 탈취를 막는다고 함
     */
    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * get unique data to find user
     */
    private String getPk(String token) {
        return (String) Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().get("email");
    }

    public String getPkFromAuthentication() throws NullPointerException{
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) auth.getPrincipal()).getEmail();
        log.info("getPkFromAUthentication: email="+email );
        if(email== null) throw new NullPointerException("User not signed in");
        return email;
    }

    /**
     * @throws NullPointerException header에 AUTHORIZATION 키 없으면 발생
     */
    public String resolveToken(HttpServletRequest request) throws NullPointerException {
        String token = null;
        try {
            token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(JWT_PREFIX.length());
        } catch (NullPointerException e) {
            log.warn(e.getMessage());
            throw new NullPointerException("JWT is not included in request");
        }
        return token;
    }

    public boolean isTokenValid(String token) throws RuntimeException {
        log.info("JWTProvider validateToken: " +token);
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.info("accessToken validation : expired");
            throw new JwtValidationException("JWT is expired", null, HttpStatus.REQUEST_TIMEOUT);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            // Claims에 담았던 인자의 키밸류들이 아님
            throw new JwtValidationException("JWT's type or value is invalid", e, HttpStatus.UNAUTHORIZED);
        } catch (SignatureException e) {
            // signature이 유효하지 않음
            throw new JwtValidationException("JWT's signature is not invalid", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return true;
    }
}
