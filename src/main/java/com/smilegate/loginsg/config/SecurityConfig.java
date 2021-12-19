package com.smilegate.loginsg.config;

import com.smilegate.loginsg.config.jwt.CustomFilterExceptionHandler;
import com.smilegate.loginsg.config.jwt.JWTAuthticationFilter;
import com.smilegate.loginsg.config.jwt.JWTProvider;
import com.smilegate.loginsg.config.jwt.JwtAccessDeniedHandler;
import com.smilegate.loginsg.domain.Role;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTProvider jwtProvider;
    private final CustomFilterExceptionHandler customFilterExceptionHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

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
            .exceptionHandling()
            .accessDeniedHandler(jwtAccessDeniedHandler) //권한별 접근 제어
        .and()
            .authorizeRequests()
            .antMatchers("/user/register", "/user/login","/user/reissueactoken", "/user/mailpw").anonymous()
            .antMatchers("/user/logout").authenticated()
            .antMatchers("/admin/**").hasAuthority(Role.ADMIN.toString())
            .antMatchers("/**").permitAll()
        .and()
            // CustomFilterExceptionHandler -> JWTAuthenticationFIlter -> UsernamePasswordAuthenticationFilter
            .addFilterBefore(new JWTAuthticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(customFilterExceptionHandler, JWTAuthticationFilter.class);
    }
}
