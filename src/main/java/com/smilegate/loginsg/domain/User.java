package com.smilegate.loginsg.domain;

import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

@NoArgsConstructor
@Entity
public class User {

    @Id @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email @Column(unique = true)
    private String email;
    private String password;
    private String name;
    private Role role;

    public static User createMember(RegisterRequestDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(dto.getPassword())
                .role(Role.MEMBER)
                .build();
    }

    @Builder
    public User(String email, String name,String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }
}
