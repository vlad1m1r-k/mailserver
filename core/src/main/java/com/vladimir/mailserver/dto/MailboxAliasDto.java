package com.vladimir.mailserver.dto;

public class MailboxAliasDto {
    private Long id;
    private String name;
    private Boolean isDefault;

    private MailboxAliasDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.isDefault = builder.isDefault;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Boolean isDefault;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder isDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public MailboxAliasDto build() {
            return new MailboxAliasDto(this);
        }
    }
}
