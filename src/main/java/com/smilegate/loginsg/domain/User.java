package com.smilegate.loginsg.domain;

import com.smilegate.loginsg.web.dto.RegisterRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class User implements UserDetails {

    @Id @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email @Column(length = 25, unique = true)
    private String email;

    @Column(columnDefinition="char(60)")
    private String password;

    @Column(length = 10)
    private String name;

    @Enumerated(EnumType.STRING) @Column(columnDefinition = "char(6)")
    private Role role;

    private String refreshToken;

    public static User createMember(RegisterRequestDto dto, String encrypted) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(encrypted)
                .role(Role.MEMBER)
                .build();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Builder
    public User(String email, String name,String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> res = new ArrayList<>();
        res.add(new SimpleGrantedAuthority(role.toString()));
        return res;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
