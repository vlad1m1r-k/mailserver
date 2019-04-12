package com.vladimir.mailserver.dto;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailDtoTest {
    @Test
    public void testMailDto() {
        List<AttachmentDto> attachmentDtos = new ArrayList<>();
        attachmentDtos.add(new AttachmentDto.Builder().build());
        MailDto dto = new MailDto.Builder()
                .id(2L)
                .from("from")
                .to("to")
                .subject("subject")
                .message("message")
                .date(new Date("Wed Apr 10 10:13:23 UTC 2019"))
                .attachments(attachmentDtos)
                .build();
        Assert.assertEquals(new Long(2), dto.getId());
        Assert.assertEquals("from", dto.getFrom());
        Assert.assertEquals("to", dto.getTo());
        Assert.assertEquals("subject", dto.getSubject());
        Assert.assertEquals("message", dto.getMessage());
        Assert.assertEquals("2019-04-10 13:13:23", dto.getDate());
        Assert.assertEquals(1, dto.getAttachments().size());
    }
}