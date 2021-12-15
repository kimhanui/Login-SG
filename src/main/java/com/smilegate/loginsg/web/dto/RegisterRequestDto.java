package com.smilegate.loginsg.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
public class RegisterRequestDto {

    @Email
    private String email; // id

    @Min(4) @NotBlank
    private String password;

    @NotBlank
    private String name;
}
