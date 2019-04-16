package com.vladimir.mailserver.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private Long size;
    private String type;

    @Lob
    private byte[] body;

    @ManyToOne
    @JoinColumn(name = "mail_id")
    private Mail mail;

    public Attachment() {
    }

    public Attachment(String name, Long size, String type, byte[] body) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.body = body;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public byte[] getBody() {
        return body;
    }

    public Mail getMail() {
        return mail;
    }

    public String getType() {
        return type;
    }
}
