package com.smilegate.loginsg.service;

import com.smilegate.loginsg.domain.User;
import com.smilegate.loginsg.domain.UserRepository;
import com.smilegate.loginsg.web.dto.AdminUpdateUserDto;
import com.smilegate.loginsg.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 매니저 권한 확인x (spring filter에서 함)
 */
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly=true)
    public List<UserDto> getUserList() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUserInfo(AdminUpdateUserDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new NullPointerException("존재하지 않는 계정입니다."));
        user.updateFromDto(dto, passwordEncoder.encode(dto.getPassword()));
        return UserDto.fromEntity(user);
    }
}
