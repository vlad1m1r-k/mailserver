package com.vladimir.mailserver.events;

import com.vladimir.mailserver.domain.Attachment;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class NewMailEvent extends ApplicationEvent {
    private String from;
    private String to;
    private String subject;
    private String body;
    private List<Attachment> attachments;

    public NewMailEvent(Object source, String from, String to, String subject, String body) {
        super(source);
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public NewMailEvent(Object source, String from, String to, String subject, String body, List<Attachment> attachments) {
        super(source);
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
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

    public String getBody() {
        return body;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
