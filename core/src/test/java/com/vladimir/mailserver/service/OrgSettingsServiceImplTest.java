package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.AcceptedDomain;
import com.vladimir.mailserver.domain.MailboxAlias;
import com.vladimir.mailserver.domain.OrganizationSettings;
import com.vladimir.mailserver.dto.OrgSettingsDto;
import com.vladimir.mailserver.repository.AcceptedDomainRepository;
import com.vladimir.mailserver.repository.OrganizationSettingsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrgSettingsServiceImpl.class)
public class OrgSettingsServiceImplTest {
    @Autowired
    private OrgSettingsServiceImpl oss;

    @MockBean
    private OrganizationSettingsRepository orgSettingsRepository;

    @MockBean
    private AcceptedDomainRepository acceptedDomainRepository;

    @Test
    public void testGetDefaultDomainSuccess() {
        OrganizationSettings settings = Mockito.mock(OrganizationSettings.class);
        Mockito.when(orgSettingsRepository.findOne()).thenReturn(settings);
        Set<AcceptedDomain> domainSet = new HashSet<>();
        AcceptedDomain acceptedDomain = new AcceptedDomain("domain1", null);
        acceptedDomain.setDefault(true);
        domainSet.add(acceptedDomain);
        domainSet.add(new AcceptedDomain("localhost", null));
        domainSet.add(new AcceptedDomain("domain2", null));
        domainSet.add(new AcceptedDomain("domain3", null));
        domainSet.add(new AcceptedDomain("domain4", null));
        Mockito.when(settings.getAcceptedDomains()).thenReturn(domainSet);
        AcceptedDomain domain = oss.getDefaultDomain();
        Assert.assertEquals(acceptedDomain, domain);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetDefaultDomainException() {
        OrganizationSettings settings = Mockito.mock(OrganizationSettings.class);
        Mockito.when(orgSettingsRepository.findOne()).thenReturn(settings);
        Set<AcceptedDomain> domainSet = new HashSet<>();
        AcceptedDomain acceptedDomain = new AcceptedDomain("domain1", null);
        domainSet.add(acceptedDomain);
        domainSet.add(new AcceptedDomain("localhost", null));
        domainSet.add(new AcceptedDomain("domain2", null));
        domainSet.add(new AcceptedDomain("domain3", null));
        domainSet.add(new AcceptedDomain("domain4", null));
        Mockito.when(settings.getAcceptedDomains()).thenReturn(domainSet);
        oss.getDefaultDomain();
    }

    @Test
    public void testGetDomainByName() {
        oss.getDomainByName("name");
        Mockito.verify(acceptedDomainRepository).findAcceptedDomainByName(ArgumentMatchers.eq("name"));
    }

    @Test
    public void testIsDomainLocalTrue() {
        OrganizationSettings settings = Mockito.mock(OrganizationSettings.class);
        Mockito.when(orgSettingsRepository.findOne()).thenReturn(settings);
        Set<AcceptedDomain> domainSet = new HashSet<>();
        domainSet.add(new AcceptedDomain("localhost", null));
        domainSet.add(new AcceptedDomain("domain2", null));
        domainSet.add(new AcceptedDomain("domain3", null));
        domainSet.add(new AcceptedDomain("domain4", null));
        Mockito.when(settings.getAcceptedDomains()).thenReturn(domainSet);
        boolean result = oss.isDomainLocal("domain2");
        Assert.assertTrue(result);
    }

    @Test
    public void testIsDomainLocalFalse() {
        OrganizationSettings settings = Mockito.mock(OrganizationSettings.class);
        Mockito.when(orgSettingsRepository.findOne()).thenReturn(settings);
        Set<AcceptedDomain> domainSet = new HashSet<>();
        domainSet.add(new AcceptedDomain("localhost", null));
        domainSet.add(new AcceptedDomain("domain2", null));
        domainSet.add(new AcceptedDomain("domain3", null));
        domainSet.add(new AcceptedDomain("domain4", null));
        Mockito.when(settings.getAcceptedDomains()).thenReturn(domainSet);
        boolean result = oss.isDomainLocal("domain");
        Assert.assertFalse(result);
    }

    @Test
    public void testGetOrgSettingsSuccess() {
        OrganizationSettings settings = Mockito.mock(OrganizationSettings.class);
        Mockito.when(orgSettingsRepository.findOne()).thenReturn(settings);
        Mockito.when(settings.getName()).thenReturn("name");
        Set<AcceptedDomain> domainSet = new HashSet<>();
        domainSet.add(new AcceptedDomain("localhost", null));
        domainSet.add(new AcceptedDomain("domain2", null));
        domainSet.add(new AcceptedDomain("domain3", null));
        domainSet.add(new AcceptedDomain("domain4", null));
        Mockito.when(settings.getAcceptedDomains()).thenReturn(domainSet);
        OrgSettingsDto orgSettings = oss.getOrgSettings();
        Assert.assertEquals("name", orgSettings.getName());
        Assert.assertEquals(4, orgSettings.getDomains().size());
    }

    @Test
    public void testSetNameSuccess() {
        OrganizationSettings settings = Mockito.mock(OrganizationSettings.class);
        Mockito.when(orgSettingsRepository.findOne()).thenReturn(settings);
        oss.setName("newname");
        Mockito.verify(settings).setName(ArgumentMatchers.eq("newname"));
        Mockito.verify(orgSettingsRepository).save(ArgumentMatchers.eq(settings));
    }

    @Test
    public void testAddDomainSuccess() {
        final AcceptedDomain[] domains = new AcceptedDomain[1];
        Mockito.when(acceptedDomainRepository.save(Mockito.any())).then(o -> domains[0] = o.getArgument(0));
        oss.addDomain("name");
        Mockito.verify(acceptedDomainRepository).save(ArgumentMatchers.any());
        Assert.assertEquals("name", domains[0].getName());
    }

    @Test
    public void testDeleteDomainSuccess() {
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.isDefault()).thenReturn(false);
        Mockito.when(domain.getAliases()).thenReturn(new HashSet<>());
        Mockito.when(acceptedDomainRepository.findAcceptedDomainById(Mockito.anyLong())).thenReturn(domain);
        boolean result = oss.deleteDomain(2L);
        Mockito.verify(acceptedDomainRepository).findAcceptedDomainById(ArgumentMatchers.eq(2L));
        Mockito.verify(acceptedDomainRepository).deleteById(ArgumentMatchers.eq(2L));
        Assert.assertTrue(result);
    }

    @Test
    public void testDeleteDomainFailByDefault() {
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.isDefault()).thenReturn(true);
        Mockito.when(domain.getAliases()).thenReturn(new HashSet<>());
        Mockito.when(acceptedDomainRepository.findAcceptedDomainById(Mockito.anyLong())).thenReturn(domain);
        boolean result = oss.deleteDomain(2L);
        Mockito.verify(acceptedDomainRepository).findAcceptedDomainById(ArgumentMatchers.eq(2L));
        Mockito.verify(acceptedDomainRepository, Mockito.never()).deleteById(ArgumentMatchers.any());
        Assert.assertFalse(result);
    }

    @Test
    public void testDeleteDomainFailByAliases() {
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.isDefault()).thenReturn(false);
        Mockito.when(domain.getAliases()).thenReturn(new HashSet<>(Arrays.asList(new MailboxAlias(), new MailboxAlias())));
        Mockito.when(acceptedDomainRepository.findAcceptedDomainById(Mockito.anyLong())).thenReturn(domain);
        boolean result = oss.deleteDomain(2L);
        Mockito.verify(acceptedDomainRepository).findAcceptedDomainById(ArgumentMatchers.eq(2L));
        Mockito.verify(acceptedDomainRepository, Mockito.never()).deleteById(ArgumentMatchers.any());
        Assert.assertFalse(result);
    }

    @Test
    public void testEditDomainSuccess() {
        AcceptedDomain domain = new AcceptedDomain();
        Mockito.when(acceptedDomainRepository.findAcceptedDomainById(Mockito.anyLong())).thenReturn(domain);
        oss.editDomain(2L, "newname", false);
        Mockito.verify(orgSettingsRepository, Mockito.never()).findOne();
        Mockito.verify(acceptedDomainRepository).findAcceptedDomainById(ArgumentMatchers.eq(2L));
        Assert.assertEquals("newname", domain.getName());
        Assert.assertFalse(domain.isDefault());
        Mockito.verify(acceptedDomainRepository).save(ArgumentMatchers.eq(domain));
    }

    public void testEditDomainDoDefaultSuccess() {
        OrganizationSettings settings = Mockito.mock(OrganizationSettings.class);
        Mockito.when(orgSettingsRepository.findOne()).thenReturn(settings);
        Set<AcceptedDomain> domainSet = new HashSet<>();
        AcceptedDomain acceptedDomain = new AcceptedDomain("domain1", null);
        acceptedDomain.setDefault(true);
        domainSet.add(acceptedDomain);
        Mockito.when(settings.getAcceptedDomains()).thenReturn(domainSet);
        AcceptedDomain domain = new AcceptedDomain();
        Mockito.when(acceptedDomainRepository.findAcceptedDomainById(Mockito.anyLong())).thenReturn(domain);
        oss.editDomain(2L, "newname", true);
        Assert.assertFalse(acceptedDomain.isDefault());
        Mockito.verify(acceptedDomainRepository).save(ArgumentMatchers.eq(acceptedDomain));
        Mockito.verify(acceptedDomainRepository).findAcceptedDomainById(ArgumentMatchers.eq(2L));
        Assert.assertTrue(domain.isDefault());
        Assert.assertEquals("newname", domain.getName());
        Mockito.verify(acceptedDomainRepository).save(ArgumentMatchers.eq(domain));
    }
}