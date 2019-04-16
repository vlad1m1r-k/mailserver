package com.vladimir.mailserver.dto;

import org.junit.Assert;
import org.junit.Test;

public class AttachmentDtoTest {
    @Test
    public void testAttachmentDto() {
        AttachmentDto dto = new AttachmentDto.Builder()
                .id(2L)
                .name("name")
                .size(3L)
                .build();
        Assert.assertEquals(new Long(2), dto.getId());
        Assert.assertEquals("name", dto.getName());
        Assert.assertEquals(new Long(3), dto.getSize());
    }
}