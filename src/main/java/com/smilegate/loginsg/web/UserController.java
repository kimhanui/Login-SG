package com.smilegate.loginsg.web;

import com.smilegate.loginsg.service.UserService;
import com.smilegate.loginsg.web.dto.LoginRequestDto;
import com.smilegate.loginsg.web.dto.LoginResponseDto;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDto dto) throws IllegalArgumentException {
        log.info("registerUser:" + dto.toString());
        return userService.registerUser(dto);
    }

    @GetMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto dto) throws IllegalArgumentException, NullPointerException {
        log.info("loginUser:" + dto.toString());
        return userService.loginUser(dto);
    }
}