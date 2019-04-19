package com.vladimir.mailserver.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "mailboxes")
public class Mailbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private MailUser user;

    @OneToMany(mappedBy = "mailbox")
    private Set<Mail> mails = new HashSet<>();

    @OneToMany(mappedBy = "mailbox")
    private Set<MailboxAlias> aliases = new HashSet<>();

    public Mailbox() {
    }

    public Mailbox(MailUser user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public MailUser getUser() {
        return user;
    }

    public MailboxAlias getDefaultAlias() {
        Optional<MailboxAlias> defaultAlias = aliases.stream().filter(MailboxAlias::isDefault).findFirst();
        if (defaultAlias.isPresent()) {
            return defaultAlias.get();
        }
        throw new IllegalStateException("No default alias found");
    }

    public Set<MailboxAlias> getAliases() {
        return aliases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mailbox mailbox = (Mailbox) o;
        return id == mailbox.id &&
                user.equals(mailbox.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }
}
