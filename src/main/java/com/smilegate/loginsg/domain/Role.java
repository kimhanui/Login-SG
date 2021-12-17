package com.smilegate.loginsg.domain;

import lombok.Getter;

@Getter
public enum Role {
    MEMBER("member"),
    ADMIN("admin");

    private final String name;

    Role(String name){
        this.name = name;
    }
}
