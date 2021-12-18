package com.smilegate.loginsg.service;

import com.smilegate.loginsg.config.jwt.JWTProvider;
import com.smilegate.loginsg.domain.User;
import com.smilegate.loginsg.domain.UserRepository;
import com.smilegate.loginsg.utils.Mailing;
import com.smilegate.loginsg.web.dto.LoginRequestDto;
import com.smilegate.loginsg.web.dto.LoginResponseDto;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final JWTProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String SUCCESS_RESPONSE = "success";
    private static final char[] ALLOWED_SYMBOLS = new char[]{'!', '@', '#', '%', '^', '&', '*'};

    @Transactional
    public String registerUser(RegisterRequestDto dto) throws IllegalArgumentException {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent()) throw new IllegalArgumentException("User is already exist");
        if (!isValidPassword(dto.getPassword())) throw new IllegalArgumentException("Password is not valid");
        // 비밀번호 암호화
        String encrypted = passwordEncoder.encode(dto.getPassword());
        userRepository.save(User.createMember(dto, encrypted));
        return SUCCESS_RESPONSE;
    }


    @Transactional
    public LoginResponseDto loginUser(LoginRequestDto dto) throws NullPointerException, IllegalArgumentException {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NullPointerException("User not found"));
        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password not correct");
        }
        // jwt 발급
        String accessToken = jwtProvider.createAccessToken(dto.getEmail());
        String refreshToken = jwtProvider.createRefreshToken();
        user.updateRefreshToken(refreshToken);
        return new LoginResponseDto(dto.getEmail(), accessToken, user.getRole().toString());
    }


    @Transactional
    public String tryToMatchMyPassword(String rawPassword) {
        String result = "";
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String email = ((User) authentication.getDetails()).getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("User not found"));

        // wrongCnt 확인 후 비밀번호 맞추기 시도
        if (user.getWrongCnt() <= 3) {
            if ((passwordEncoder.matches(rawPassword, user.getPassword()))) {
                user.resetWrongCnt();
                result = "correct";
            } else {
                user.addWrongCnt();
                result = "incorrect";
            }
        } else {
            Mailing.sendMailForResetPW(email);
            result = "exceeded try times so mailing";
        }
        return result;
    }

    /**
     * condition:
     * - at least 4 words
     * - only contains these: alphabet(small or large), special symbol('!','@','#','%','^','&','*'), digit(0~9)
     */
    private boolean isValidPassword(String password) {
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
