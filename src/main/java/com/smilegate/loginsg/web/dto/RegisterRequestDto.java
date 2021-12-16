package com.smilegate.loginsg.web.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterRequestDto {

    @Email
    private String email; // id

    @Min(4) @Max(12) @NotBlank
    private String password;

    @Max(10) @NotBlank
    private String name;
}
