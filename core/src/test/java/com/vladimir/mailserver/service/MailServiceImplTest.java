package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.Mailbox;
import com.vladimir.mailserver.domain.MailboxAlias;
import com.vladimir.mailserver.dto.MailDto;
import com.vladimir.mailserver.events.NewMailEvent;
import com.vladimir.mailserver.repository.MailRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailServiceImpl.class)
public class MailServiceImplTest {
    private MailServiceImpl msi;

    @MockBean
    private UserService userService;

    @MockBean
    private MailRepository mailRepository;

    @MockBean
    private AttachmentService attachmentService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    private Pageable pageable = Pageable.unpaged();

    @Before
    public void init() {
        msi = new MailServiceImpl(userService, mailRepository, applicationEventPublisher, attachmentService);
    }

    @Test
    public void testSendSuccess() throws IOException {
        List<MultipartFile> fileList = new ArrayList<>();
        MultipartFile multipartFile = new MockMultipartFile("name", "filename", "type", "body".getBytes());
        fileList.add(multipartFile);
        MailUser user = Mockito.mock(MailUser.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        MailboxAlias alias = Mockito.mock(MailboxAlias.class);
        Mockito.when(mailbox.getDefaultAlias()).thenReturn(alias);
        Mockito.when(alias.getValue()).thenReturn("user@localhost");
        final Mail[] mail = new Mail[1];
        Mockito.when(mailRepository.save(Mockito.any())).then(o -> mail[0] = o.getArgument(0));
        final Object[] attachments = new Object[1];
        Mockito.doAnswer(o -> attachments[0] = o.getArgument(1)).when(attachmentService).save(Mockito.any(), Mockito.any());
        final NewMailEvent[] events = new NewMailEvent[1];
        Mockito.doAnswer(o -> events[0] = o.getArgument(0)).when(applicationEventPublisher).publishEvent(Mockito.any());
        msi.send("login", "to", "subject", "body", fileList);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).save(ArgumentMatchers.any(Mail.class));
        Assert.assertEquals(MailType.SENT, mail[0].getType());
        Assert.assertEquals(mailbox, mail[0].getMailbox());
        Assert.assertEquals("user@localhost", mail[0].getFrom());
        Assert.assertEquals("to", mail[0].getTo());
        Assert.assertEquals("subject", mail[0].getSubject());
        Assert.assertEquals("body", mail[0].getMessage());
        Assert.assertNotNull(mail[0].getDate());
        Mockito.verify(attachmentService).save(ArgumentMatchers.eq(mail[0]), ArgumentMatchers.any());
        List<Attachment> attachmentList = (List<Attachment>) attachments[0];
        Assert.assertEquals(multipartFile.getOriginalFilename(), attachmentList.get(0).getName());
        Assert.assertEquals(new Long(multipartFile.getSize()), attachmentList.get(0).getSize());
        Assert.assertEquals(multipartFile.getContentType(), attachmentList.get(0).getType());
        Assert.assertEquals(multipartFile.getBytes(), attachmentList.get(0).getBody());
        Mockito.verify(applicationEventPublisher).publishEvent(ArgumentMatchers.any());
        Assert.assertEquals("user@localhost", events[0].getFrom());
        Assert.assertEquals("to", events[0].getTo());
        Assert.assertEquals("to", events[0].getTo());
        Assert.assertEquals("body", events[0].getBody());
        Assert.assertEquals(attachments[0], events[0].getAttachments());
    }

    @Test
    public void testSaveSuccess() {
        Mail mail = new Mail(MailType.INCOMING, null, "from", "to", "subject", "message", null);
        msi.save(mail);
        Mockito.verify(mailRepository).save(ArgumentMatchers.eq(mail));
    }

    @Test
    public void testGetMailsSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        List<Mail> mails = new ArrayList<>();
        mails.add(new Mail(MailType.INCOMING, null, "from", "to", "subject", "message", null));
        mails.add(new Mail(MailType.INCOMING, null, "from", "to", "subject", "message", null));
        mails.add(new Mail(MailType.INCOMING, null, "from", "to", "subject", "message", null));
        Page<Mail> mailPages = new PageImpl<>(mails);
        Mockito.when(mailRepository.findAllByMailboxAndTypeAndIsDeleted(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mailPages);
        Page<MailDto> result = msi.getMails("login", MailType.INCOMING, pageable);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findAllByMailboxAndTypeAndIsDeleted(ArgumentMatchers.eq(mailbox),
                ArgumentMatchers.eq(MailType.INCOMING), ArgumentMatchers.eq(false), ArgumentMatchers.eq(pageable));
        Assert.assertEquals(3, result.getContent().size());
        Assert.assertEquals("from", result.getContent().get(0).getFrom());
        Assert.assertEquals("to", result.getContent().get(0).getTo());
        Assert.assertEquals("subject", result.getContent().get(0).getSubject());
        Assert.assertEquals("message", result.getContent().get(0).getMessage());
    }

    @Test
    public void testGetDeletedMailsSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        List<Mail> mails = new ArrayList<>();
        mails.add(new Mail(MailType.DELETED_SENT, null, "from", "to", "subject", "message", null));
        mails.add(new Mail(MailType.DELETED_INC, null, "from", "to", "subject", "message", null));
        mails.add(new Mail(MailType.DELETED_INC, null, "from", "to", "subject", "message", null));
        Page<Mail> mailPages = new PageImpl<>(mails);
        Mockito.when(mailRepository.findDeleted(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mailPages);
        Page<MailDto> result = msi.getDeletedMails("login", pageable);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findDeleted(ArgumentMatchers.eq(mailbox), ArgumentMatchers.eq(false),
                ArgumentMatchers.eq(MailType.DELETED_INC), ArgumentMatchers.eq(MailType.DELETED_SENT), ArgumentMatchers.eq(pageable));
        Assert.assertEquals(3, result.getContent().size());
        Assert.assertEquals("from", result.getContent().get(0).getFrom());
        Assert.assertEquals("to", result.getContent().get(0).getTo());
        Assert.assertEquals("subject", result.getContent().get(0).getSubject());
        Assert.assertEquals("message", result.getContent().get(0).getMessage());
    }

    @Test
    public void testDeleteIncomingSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.INCOMING, mailbox, "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.delete("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository).save(ArgumentMatchers.eq(mail));
        Assert.assertEquals(MailType.DELETED_INC, mail.getType());
        Assert.assertFalse(mail.getDeleted());
    }

    @Test
    public void testDeleteSendSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.SENT, mailbox, "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.delete("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository).save(ArgumentMatchers.eq(mail));
        Assert.assertEquals(MailType.DELETED_SENT, mail.getType());
        Assert.assertFalse(mail.getDeleted());
    }

    @Test
    public void testDeleteDeletedIncSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.DELETED_INC, mailbox, "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.delete("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository).save(ArgumentMatchers.eq(mail));
        Assert.assertTrue(mail.getDeleted());
    }

    @Test
    public void testDeleteDeletedSentSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.DELETED_SENT, mailbox, "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.delete("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository).save(ArgumentMatchers.eq(mail));
        Assert.assertTrue(mail.getDeleted());
    }

    @Test
    public void testDeleteFail() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.INCOMING, new Mailbox(), "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.delete("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void testRestoreIncSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.DELETED_INC, mailbox, "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.restore("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository).save(ArgumentMatchers.eq(mail));
        Assert.assertEquals(MailType.INCOMING, mail.getType());
    }

    @Test
    public void testRestoreSentSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.DELETED_SENT, mailbox, "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.restore("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository).save(ArgumentMatchers.eq(mail));
        Assert.assertEquals(MailType.SENT, mail.getType());
    }

    @Test
    public void testRestoreFail() {
        MailUser user = Mockito.mock(MailUser.class);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        Mail mail = new Mail(MailType.DELETED_SENT, new Mailbox(), "from", "to", "subject", "message", null);
        Mockito.when(mailRepository.findMailById(Mockito.anyLong())).thenReturn(mail);
        msi.restore("login", 2L);
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(mailRepository).findMailById(ArgumentMatchers.eq(2L));
        Mockito.verify(mailRepository, Mockito.never()).save(ArgumentMatchers.any());
    }
}