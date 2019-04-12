package com.vladimir.mailserver.dto;

import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;

public class MailUserDto {
    private Long id;
    private String name;
    private String surname;
    private String login;
    private UserRoles role;
    private String defaultAlias;
    private UserStatus status;

    private MailUserDto(Builder builder) {
        this.name = builder.name;
        this.surname = builder.surname;
        this.login = builder.login;
        this.role = builder.role;
        this.defaultAlias = builder.defaultAlias;
        this.id = builder.id;
        this.status = builder.status;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getLogin() {
        return login;
    }

    public UserRoles getRole() {
        return role;
    }

    public String getDefaultAlias() {
        return defaultAlias;
    }

    public Long getId() {
        return id;
    }

    public UserStatus getStatus() {
        return status;
    }

    public static class Builder {
        private Long id;
        private String name;
        private String surname;
        private String login;
        private UserRoles role;
        private String defaultAlias;
        private UserStatus status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder role(UserRoles role) {
            this.role = role;
            return this;
        }

        public Builder defaultAlias(String alias) {
            this.defaultAlias = alias;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public MailUserDto build() {
            return new MailUserDto(this);
        }
    }
}
