package com.vladimir.mailserver.dto;

import org.junit.Assert;
import org.junit.Test;

public class AcceptedDomainsDtoTest {
    @Test
    public void testAcceptedDomainDto() {
        AcceptedDomainsDto dto = AcceptedDomainsDto.builder()
                .id(2L)
                .name("name")
                .isDefault(false)
                .build();
        Assert.assertEquals(new Long(2), dto.getId());
        Assert.assertEquals("name", dto.getName());
        Assert.assertFalse(dto.getDefault());
    }
}