package com.vladimir.mailserver.dto;

import java.util.Date;
import java.util.List;

public class MailDto {
    private Long id;
    private String from;
    private String to;
    private String subject;
    private String message;
    private String date;
    private List<AttachmentDto> attachments;

    private MailDto(Builder builder) {
        this.id = builder.id;
        this.from = builder.from;
        this.to = builder.to;
        this.subject = builder.subject;
        this.message = builder.message;
        this.date = String.format("%1$tF %1$tT", builder.date);
        this.attachments = builder.attachments;
    }

    public Long getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public static class Builder {
        private Long id;
        private String from;
        private String to;
        private String subject;
        private String message;
        private Date date;
        private List<AttachmentDto> attachments;

        public Builder id (Long id) {
            this.id = id;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder attachments(List<AttachmentDto> attachments) {
            this.attachments = attachments;
            return this;
        }

        public MailDto build() {
            return new MailDto(this);
        }
    }
}
