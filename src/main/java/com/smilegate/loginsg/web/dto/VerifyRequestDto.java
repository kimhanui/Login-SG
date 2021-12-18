package com.smilegate.loginsg.web.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * cf. login, find password
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerifyRequestDto {

    @Email
    private String email;

    @Min(4) @Max(12) @NotBlank
    private String password;
}
