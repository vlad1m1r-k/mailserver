package com.vladimir.mailserver.dto;

import java.util.List;

public class OrgSettingsDto {
    private String name;
    private List<AcceptedDomainsDto> domains;

    private OrgSettingsDto(Builder builder) {
        this.name = builder.name;
        this.domains = builder.domains;
    }

    public String getName() {
        return name;
    }

    public List<AcceptedDomainsDto> getDomains() {
        return domains;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<AcceptedDomainsDto> domains;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder domains(List<AcceptedDomainsDto> domains) {
            this.domains = domains;
            return this;
        }

        public OrgSettingsDto build() {
            return new OrgSettingsDto(this);
        }
    }
}
