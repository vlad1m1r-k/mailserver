package com.vladimir.mailserver.dto;

import org.junit.Assert;
import org.junit.Test;

public class MailboxAliasDtoTest {
    @Test
    public void testMailboxAliasDto() {
        MailboxAliasDto dto = new MailboxAliasDto.Builder()
                .id(2L)
                .name("name")
                .isDefault(true)
                .build();
        Assert.assertEquals(new Long(2), dto.getId());
        Assert.assertEquals("name", dto.getName());
        Assert.assertTrue(dto.getDefault());
    }
}