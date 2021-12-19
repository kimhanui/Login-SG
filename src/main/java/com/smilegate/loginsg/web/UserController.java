package com.smilegate.loginsg.web;

import com.smilegate.loginsg.service.UserService;
import com.smilegate.loginsg.web.dto.ReIssueDto;
import com.smilegate.loginsg.web.dto.VerifyRequestDto;
import com.smilegate.loginsg.web.dto.VerifyResponseDto;
import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDto dto) throws IllegalArgumentException {
        log.info("registerUser:" + dto.toString());
        String res = userService.registerUser(dto);
        Map<String, String> map = Map.of(
                "result",res
        );
        return ResponseEntity.ok(map);
    }

    @PostMapping("/login")
    public ResponseEntity<VerifyResponseDto> login(@Valid @RequestBody VerifyRequestDto dto) throws IllegalArgumentException, NullPointerException {
        log.info("loginUser:" + dto.toString());
        VerifyResponseDto res = userService.loginUser(dto);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() throws RuntimeException {
        log.info("logout");
        String res = userService.logoutUser();
        Map<String, String> map = Map.of(
                "result",res
        );
        return ResponseEntity.ok(map);
    }

    @GetMapping("/mailpw")
    public ResponseEntity<Map<String, String>> sendEmailToGetNewPassowrd(@Valid @RequestParam @Email String email) throws RuntimeException {
        String res = userService.sendEmailToGetNewPassowrd(email);
        Map<String, String> map = Map.of(
                "result",res
        );
        return ResponseEntity.ok(map);
    }

    @PostMapping("/reissueactoken")
    public ResponseEntity<Map<String, String>> reIssueAccessToken(@Valid @RequestBody ReIssueDto dto) throws RuntimeException {
        log.info("ReIssueDto email="+dto.getEmail()+" refreshToken=" +dto.getRefreshToken());
        String accessToken = userService.reIssueAccessToken(dto);
        Map<String, String> map = Map.of(
                "accessToken",accessToken
        );
        return ResponseEntity.ok(map);
    }
}