package com.smilegate.loginsg.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JWTProvider {

    private final UserDetailsService userDetailsService;
    private static final String JWT_PREFIX = "Bearer "; // TODO: 의미
    private final String SECRET_KEY;
    private final long ACCESS_VALID_TIME;
    private final long REFRESH_VALID_TIME;

    @Autowired
    public JWTProvider(@Value("${jwt.secret-key}") String secretKey, @Value("${jwt.access-valid-time}")long accessValidTime, @Value("${jwt.refresh-valid-time}")long refreshValidTime,
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
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                //payload
                .setSubject("userRefreshToken") // token 제목
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_VALID_TIME))
                //signature
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * JWT -> Authentication for Spring Boot
     */
    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    /**
     * get unique data to find user
     */
    private String getPk(String token){
        return (String) Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().get("email");
    }
}
