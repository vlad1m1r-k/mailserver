package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.AcceptedDomain;
import com.vladimir.mailserver.dto.OrgSettingsDto;

public interface OrgSettingsService {
    AcceptedDomain getDefaultDomain();
    AcceptedDomain getDomainByName(String name);
    boolean isDomainLocal(String domain);
    OrgSettingsDto getOrgSettings();
    void setName(String name);
    void addDomain(String name);
    boolean deleteDomain(Long id);
    void editDomain(Long id, String name, Boolean toDefault);
}
