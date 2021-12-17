package com.smilegate.loginsg.config;

import com.smilegate.loginsg.config.jwt.CustomFilterExceptionHandler;
import com.smilegate.loginsg.config.jwt.JWTAuthticationFilter;
import com.smilegate.loginsg.config.jwt.JWTProvider;
import com.smilegate.loginsg.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTProvider jwtProvider;
    private final CustomFilterExceptionHandler customFilterExceptionHandler;

    @Autowired
    public SecurityConfig(JWTProvider jwtProvider, CustomFilterExceptionHandler customFilterExceptionHandler) {
        this.jwtProvider = jwtProvider;
        this.customFilterExceptionHandler = customFilterExceptionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 스프링시큐리티 앞단 설정들을 할 수 있다.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    public void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .csrf().disable()
            .cors()
        .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/user/login", "/user/register").anonymous()
            .antMatchers("/admin/**").hasAuthority(Role.ADMIN.toString())
            .antMatchers("/**").permitAll()
        .and()
            // CustomFilterExceptionHandler -> JWTAuthenticationFIlter -> UsernamePasswordAuthenticationFilter
            .addFilterBefore(new JWTAuthticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(customFilterExceptionHandler, JWTAuthticationFilter.class);
    }
}
