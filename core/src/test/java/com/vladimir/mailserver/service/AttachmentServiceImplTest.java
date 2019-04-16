package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.Mailbox;
import com.vladimir.mailserver.repository.AttachmentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AttachmentServiceImpl.class)
public class AttachmentServiceImplTest {
    @Autowired
    private AttachmentServiceImpl asi;

    @MockBean
    private AttachmentRepository attachmentRepository;

    @Test
    public void testSaveSuccess() {
        Mail mail = new Mail();
        final List<Attachment> result = new ArrayList<>();
        Mockito.when(attachmentRepository.save(Mockito.any())).then(obj -> {result.add(obj.getArgument(0)); return null;});
        List<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(new Attachment("name1", 2L, "type1", "body1".getBytes()));
        attachmentList.add(new Attachment("name2", 4L, "type2", "body3".getBytes()));
        asi.save(mail, attachmentList);
        Mockito.verify(attachmentRepository, Mockito.times(attachmentList.size())).save(ArgumentMatchers.any());
        Assert.assertEquals(mail, result.get(0).getMail());
        Assert.assertEquals("name1", result.get(0).getName());
        Assert.assertEquals(new Long(2), result.get(0).getSize());
        Assert.assertEquals("type1", result.get(0).getType());
        Assert.assertArrayEquals("body1".getBytes(), result.get(0).getBody());
        Assert.assertEquals(mail, result.get(1).getMail());
        Assert.assertEquals("name2", result.get(1).getName());
        Assert.assertEquals(new Long(4), result.get(1).getSize());
        Assert.assertEquals("type2", result.get(1).getType());
        Assert.assertArrayEquals("body3".getBytes(), result.get(1).getBody());
    }

    @Test
    public void testGetAttachmentSuccess() {
        Attachment attachment = Mockito.mock(Attachment.class);
        Mail mail = Mockito.mock(Mail.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        MailUser mailUser = Mockito.mock(MailUser.class);
        Mockito.when(mailUser.getLogin()).thenReturn("login");
        Mockito.when(mailbox.getUser()).thenReturn(mailUser);
        Mockito.when(mail.getMailbox()).thenReturn(mailbox);
        Mockito.when(attachment.getMail()).thenReturn(mail);
        Mockito.when(attachmentRepository.findAttachmentById(Mockito.anyLong())).thenReturn(attachment);
        Attachment result = asi.getAttachment("login", 2L);
        Mockito.verify(attachmentRepository, Mockito.times(1)).findAttachmentById(
                ArgumentMatchers.eq(2L));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAttachmentFail() {
        Attachment attachment = Mockito.mock(Attachment.class);
        Mail mail = Mockito.mock(Mail.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        MailUser mailUser = Mockito.mock(MailUser.class);
        Mockito.when(mailUser.getLogin()).thenReturn("login");
        Mockito.when(mailbox.getUser()).thenReturn(mailUser);
        Mockito.when(mail.getMailbox()).thenReturn(mailbox);
        Mockito.when(attachment.getMail()).thenReturn(mail);
        Mockito.when(attachmentRepository.findAttachmentById(Mockito.anyLong())).thenReturn(attachment);
        Attachment result = asi.getAttachment("login2", 2L);
        Mockito.verify(attachmentRepository, Mockito.times(1)).findAttachmentById(
                ArgumentMatchers.eq(2L));
        Assert.assertNull(result);
    }
}