package com.smilegate.loginsg.service;

import com.smilegate.loginsg.ExceptionUtil.JwtValidationException;
import com.smilegate.loginsg.config.jwt.JWTProvider;
import com.smilegate.loginsg.domain.User;
import com.smilegate.loginsg.domain.UserRepository;
import com.smilegate.loginsg.web.dto.ReIssueDto;
import com.smilegate.loginsg.web.dto.VerifyRequestDto;
import com.smilegate.loginsg.web.dto.VerifyResponseDto;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final MailingService mailingService;
    private static final String SUCCESS_RESPONSE = "success";
    private static final char[] ALLOWED_SYMBOLS = new char[]{'!', '@', '#', '%', '^', '&', '*'};

    @Transactional
    public String registerUser(RegisterRequestDto dto) throws IllegalArgumentException {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent()) throw new IllegalArgumentException("이미 가입한 회원입니다.");
        if (!isValidPassword(dto.getPassword())) throw new IllegalArgumentException("비밀번호가 조건에 맞지 않습니다.");
        // 비밀번호 암호화
        String encrypted = passwordEncoder.encode(dto.getPassword());
        userRepository.save(User.createMember(dto, encrypted));
        return SUCCESS_RESPONSE;
    }


    public VerifyResponseDto loginUser(VerifyRequestDto dto) throws NullPointerException, IllegalArgumentException {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NullPointerException("존재하지 않는 계정입니다."));
        // 비밀번호 검증 - 3회
        if (user.getWrongCnt() < 3) {
            if ((passwordEncoder.matches(dto.getPassword(), user.getPassword()))) {
                user.resetWrongCnt();
            } else {
                short wrongCnt = user.addWrongCnt();
                userRepository.saveAndFlush(user); // INFO: 바로 다음 구문에서 예외 발생하면서 @tx가 있어도 flush되지않기때문
                throw new IllegalArgumentException("인증 실패 : " + wrongCnt + "/3 회");
            }
        } else {
            throw new IllegalArgumentException("가능한 인증 시도 횟수를 초과했습니다. " +
                    "Forgot password?를 눌러 비밀번호를 재발급받으세요");
        }
        // jwt 발급
        String accessToken = jwtProvider.createAccessToken(dto.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(dto.getEmail());
        user.updateRefreshToken(refreshToken);
        userRepository.saveAndFlush(user);
        return new VerifyResponseDto(dto.getEmail(), accessToken, refreshToken, user.getRole().toString());
    }

    @Transactional
    public String logoutUser() throws NullPointerException {
        String email = jwtProvider.getPkFromAuthentication();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("존재하지 않는 계정입니다."));
        // refresh token 삭제
        user.resetRefreshToken();
        log.info("user refreshToken reset: " + user.getRefreshToken());
        return SUCCESS_RESPONSE;
    }

    @Transactional
    public String sendEmailToGetNewPassowrd(String email) throws RuntimeException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("존재하지 않는 계정입니다."));
        String randomCode = mailingService.sendMailForResetPW(email);
        user.updatePassword(passwordEncoder.encode(randomCode)); //FIXME: t-otp로 인증 후 사용자가 비번 직접 바꾸는 식으로 수정
        return SUCCESS_RESPONSE;
    }

    public String reIssueAccessToken(ReIssueDto dto) throws RuntimeException {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new NullPointerException("존재하지 않는 계정입니다."));
        //null: 로그아웃 상태
        if (user.getRefreshToken() == null || !jwtProvider.isTokenValid(user.getRefreshToken())) {
            throw new JwtValidationException("로그인 유효시간이 만료됐습니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED);
        }
        return jwtProvider.createAccessToken(dto.getEmail());
    }

    /**
     * condition:
     * - at least 4 words
     * - only contains these: alphabet(small or large), special symbol('!','@','#','%','^','&','*'), digit(0~9)
     */
    private boolean isValidPassword(String password) {
        int size = password.length();

        if (size < 4 || size > 12) return false;
        for (char c : password.toCharArray()) {
            if ('a' <= c && c <= 'z'
                    || 'A' <= c && c <= 'Z'
                    || Character.isDigit(c)) continue;
            boolean pass = false;
            for (char aSymbol : ALLOWED_SYMBOLS) {
                if (aSymbol == c) {
                    pass = true;
                    break;
                }
            }
            if(!pass) return false;
        }
        return true;
    }
}
