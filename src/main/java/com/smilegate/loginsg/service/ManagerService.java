package com.smilegate.loginsg.service;

import com.smilegate.loginsg.domain.User;
import com.smilegate.loginsg.domain.UserRepository;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import com.smilegate.loginsg.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 매니저 권한 확인x (spring filter에서 함)
 */
@RequiredArgsConstructor
@Service
public class ManagerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getUserList() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
}
