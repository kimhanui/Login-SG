package com.smilegate.loginsg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * JWT는 Cors 오류를 해결한다. 서버 응답시 아래의 정보를 헤더에 넣어주면 된다.<br>
 * Access-Control-Allow-Origin: *
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS;

    public CorsConfig(@Value("${jwt.access-valid-time}") long max_age_secs) {
        this.MAX_AGE_SECS = max_age_secs;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }
}
