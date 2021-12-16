package com.smilegate.loginsg.service;

import com.smilegate.loginsg.config.jwt.JWTProvider;
import com.smilegate.loginsg.domain.User;
import com.smilegate.loginsg.domain.UserRepository;
import com.smilegate.loginsg.web.dto.LoginRequestDto;
import com.smilegate.loginsg.web.dto.LoginResponseDto;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JWTProvider jwtProvider;
    private static final String SUCCESS_RESPONSE = "success";
    private static final char[] ALLOWED_SYMBOLS = new char[]{'!', '@', '#', '%', '^', '&', '*'};

    @Transactional
    public String registerUser(RegisterRequestDto dto) throws IllegalArgumentException {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent()) throw new IllegalArgumentException("User is already exist");
        if (!isValidatePassword(dto.getPassword())) throw new IllegalArgumentException("Password is not valid");

        userRepository.save(User.createMember(dto));
        return SUCCESS_RESPONSE;
    }

    /**
     * condition:
     * - at least 4 words
     * - only & all contains: alphabet(small or large), special symbol('!','@','#','%','^','&','*'), digit(0~9)
     */
    private boolean isValidatePassword(String password) {
        int size = password.length();

        if (size < 4) return false;
        for (char c : password.toCharArray()) {
            if (Character.isAlphabetic(c) || Character.isDigit(c)) continue;
            for (char aSymbol : ALLOWED_SYMBOLS) {
                if (aSymbol != c) return false;
            }
        }
        return true;
    }
}
