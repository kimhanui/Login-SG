package com.smilegate.loginsg.service;

import com.smilegate.loginsg.config.jwt.JWTProvider;
import com.smilegate.loginsg.domain.User;
import com.smilegate.loginsg.domain.UserRepository;
import com.smilegate.loginsg.web.dto.VerifyRequestDto;
import com.smilegate.loginsg.web.dto.VerifyResponseDto;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final MailingServivce mailingServivce;
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
    public VerifyResponseDto loginUser(VerifyRequestDto dto) throws NullPointerException, IllegalArgumentException {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NullPointerException("User not found"));
        // 비밀번호 검증 - 3회
        if (user.getWrongCnt() <= 3) {
            if ((passwordEncoder.matches(dto.getPassword(), user.getPassword()))) {
                user.resetWrongCnt();
            } else {
                user.addWrongCnt();
                throw new IllegalArgumentException("Password not correct");
            }
        }
        // jwt 발급
        String accessToken = jwtProvider.createAccessToken(dto.getEmail());
        String refreshToken = jwtProvider.createRefreshToken();
        user.updateRefreshToken(refreshToken);
        return new VerifyResponseDto(dto.getEmail(), accessToken, user.getRole().toString());
    }

    @Transactional
    public String tryToMatchMyPassword(VerifyRequestDto dto) {
        mailingServivce.sendMailForResetPW(dto.getEmail());
        return "exceeded try times. wait for a verification mail";
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
