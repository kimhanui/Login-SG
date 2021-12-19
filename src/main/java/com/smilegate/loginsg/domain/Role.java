package com.smilegate.loginsg.domain;

import lombok.Getter;

@Getter
public enum Role {
    MEMBER("member"),
    ADMIN("admin");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role fromString(String name) {
        for (Role r : values()) {
            if (r.name.equals(name)) {
                return r;
            }
        }
        return null;
    }
}
