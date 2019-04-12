package com.vladimir.mailserver.dto;

public class AttachmentDto {
    private Long id;
    private String name;
    private Long size;

    private AttachmentDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.size = builder.size;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Long size;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public AttachmentDto build() {
            return new AttachmentDto(this);
        }
    }
}
