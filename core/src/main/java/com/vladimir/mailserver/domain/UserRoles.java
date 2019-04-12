package com.vladimir.mailserver.domain;

public enum UserRoles {
    USER, ADMIN;


    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
