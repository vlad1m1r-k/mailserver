package com.vladimir.mailserver.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accepted_domains")
public class AcceptedDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private Boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationSettings organization;

    @OneToMany(mappedBy = "domain", fetch = FetchType.EAGER)
    private Set<MailboxAlias> aliases = new HashSet<>();

    public AcceptedDomain() {
    }

    public AcceptedDomain(String name, OrganizationSettings organization) {
        this.name = name.toLowerCase();
        this.organization = organization;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public Set<MailboxAlias> getAliases() {
        return aliases;
    }

    public void setDefault(Boolean toDefault) {
        this.isDefault = toDefault;
    }

    public void setName(String name) {
        this.name = name;
    }
}
