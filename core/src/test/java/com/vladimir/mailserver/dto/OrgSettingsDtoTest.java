package com.vladimir.mailserver.dto;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OrgSettingsDtoTest {
    @Test
    public void testOrgSettingsDto() {
        List<AcceptedDomainsDto> domainsDtoList = new ArrayList<>();
        domainsDtoList.add(new AcceptedDomainsDto.Builder().build());
        OrgSettingsDto dto = new OrgSettingsDto.Builder()
                .name("name")
                .domains(domainsDtoList)
                .build();
        Assert.assertEquals("name", dto.getName());
        Assert.assertEquals(1, dto.getDomains().size());
    }
}