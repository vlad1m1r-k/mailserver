package com.vladimir.mailserver.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "mails")
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private MailType type;
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "mailbox_id")
    private Mailbox mailbox;

    @Column(name = "`from`")
    private String from;

    @Column(name = "`to`")
    private String to;
    private String subject;

    @Lob
    private String message;

    private Date date;

    @OneToMany(mappedBy = "mail")
    private List<Attachment> attachments = new ArrayList<>();

    public Mail() {
    }

    public Mail(MailType type, Mailbox mailbox, String from, String to, String subject, String message, Date date) {
        this.type = type;
        this.mailbox = mailbox;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public MailType getType() {
        return type;
    }

    public Boolean getDeleted() {
        return isDeleted;
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

    public Mailbox getMailbox() {
        return mailbox;
    }

    public Date getDate() {
        return date;
    }

    public void setType(MailType type) {
        this.type = type;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
