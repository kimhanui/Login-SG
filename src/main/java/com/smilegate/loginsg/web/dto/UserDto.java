package com.smilegate.loginsg.web.dto;

import com.smilegate.loginsg.domain.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 *  <pre>
 *   - 백오피스까지 다루진 않으므로 refresh token은 x
 *   - 관리자도 비밀번호는 볼 수 없다. 단방향 해싱이기때문
 *  </pre>
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {

    @Email
    private String email;

    @NotBlank
    private String name;

    private String role;

    public static UserDto fromEntity(User user) {
        return new UserDto(user.getEmail(), user.getName(), user.getRole().getName());
    }
}
