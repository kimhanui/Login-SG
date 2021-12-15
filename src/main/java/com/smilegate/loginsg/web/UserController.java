package com.smilegate.loginsg.web;

import com.smilegate.loginsg.service.UserService;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterRequestDto dto) throws IllegalArgumentException{
        return userService.registerUser(dto);
    }
}