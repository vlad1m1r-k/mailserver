package com.vladimir.mailserver.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "aliases")
public class MailboxAlias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String value;

    @ManyToOne
    @JoinColumn(name = "mailbox_id")
    private Mailbox mailbox;
    private Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private AcceptedDomain domain;

    public MailboxAlias() {
    }

    public MailboxAlias(String value, Mailbox mailbox, Boolean isDefault, AcceptedDomain domain) {
        this.value = value;
        this.mailbox = mailbox;
        this.isDefault = isDefault;
        this.domain = domain;
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public void setDomain(AcceptedDomain domain) {
        this.domain = domain;
    }
}
