package com.smilegate.loginsg.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * cf. login, find password
 */
@AllArgsConstructor
@Getter
public class VerifyResponseDto {

    @Email
    private String email;

    @NotBlank
    private String token;

    @NotBlank
    private String role;
}
