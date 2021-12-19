package com.smilegate.loginsg.web.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerifyRequestDto {

    @Email
    private String email;

    @NotBlank
    private String password;
}
