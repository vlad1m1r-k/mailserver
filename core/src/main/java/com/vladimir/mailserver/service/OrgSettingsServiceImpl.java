package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.AcceptedDomain;
import com.vladimir.mailserver.domain.OrganizationSettings;
import com.vladimir.mailserver.dto.AcceptedDomainsDto;
import com.vladimir.mailserver.dto.OrgSettingsDto;
import com.vladimir.mailserver.repository.AcceptedDomainRepository;
import com.vladimir.mailserver.repository.OrganizationSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrgSettingsServiceImpl implements OrgSettingsService {
    private OrganizationSettingsRepository orgSettingsRepository;
    private AcceptedDomainRepository acceptedDomainRepository;

    public OrgSettingsServiceImpl(OrganizationSettingsRepository orgSettingsRepository, AcceptedDomainRepository acceptedDomainRepository) {
        this.orgSettingsRepository = orgSettingsRepository;
        this.acceptedDomainRepository = acceptedDomainRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AcceptedDomain getDefaultDomain() {
        OrganizationSettings settings = orgSettingsRepository.findOne();
        Set<AcceptedDomain> acceptedDomains = settings.getAcceptedDomains();
        Optional<AcceptedDomain> defaultDomain = acceptedDomains.stream().filter(AcceptedDomain::isDefault).findFirst();
        if (defaultDomain.isPresent()) {
            return defaultDomain.get();
        }
        throw new IllegalStateException("No default domain found");
    }

    @Override
    @Transactional(readOnly = true)
    public AcceptedDomain getDomainByName(String name) {
        return acceptedDomainRepository.findAcceptedDomainByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDomainLocal(String domain) {
        OrganizationSettings settings = orgSettingsRepository.findOne();
        Set<AcceptedDomain> acceptedDomains = settings.getAcceptedDomains();
        for (AcceptedDomain acceptedDomain : acceptedDomains) {
            if (acceptedDomain.getName().equals(domain)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public OrgSettingsDto getOrgSettings() {
        OrganizationSettings orgSettings = orgSettingsRepository.findOne();
        List<AcceptedDomainsDto> domainsDtos = orgSettings.getAcceptedDomains().stream().map(dm -> AcceptedDomainsDto.builder()
        .id(dm.getId())
        .name(dm.getName())
        .isDefault(dm.isDefault())
        .build()).collect(Collectors.toList());
        return OrgSettingsDto.builder()
                .name(orgSettings.getName())
                .domains(domainsDtos)
                .build();
    }

    @Override
    @Transactional
    public void setName(String name) {
        OrganizationSettings orgSettings = orgSettingsRepository.findOne();
        orgSettings.setName(name);
        orgSettingsRepository.save(orgSettings);
    }

    @Override
    @Transactional
    public void addDomain(String name) {
        OrganizationSettings orgSettings = orgSettingsRepository.findOne();
        AcceptedDomain domain = new AcceptedDomain(name, orgSettings);
        acceptedDomainRepository.save(domain);
    }

    @Override
    @Transactional
    public boolean deleteDomain(Long id) {
        AcceptedDomain domain = acceptedDomainRepository.findAcceptedDomainById(id);
        if (!domain.isDefault() && domain.getAliases().size() == 0) {
            acceptedDomainRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void editDomain(Long id, String name, Boolean toDefault) {
        AcceptedDomain domain;
        if (toDefault) {
            domain = getDefaultDomain();
            domain.setDefault(false);
            acceptedDomainRepository.save(domain);
        }
        domain = acceptedDomainRepository.findAcceptedDomainById(id);
        domain.setName(name);
        if (toDefault) {
            domain.setDefault(toDefault);
        }
        acceptedDomainRepository.save(domain);
    }
}
