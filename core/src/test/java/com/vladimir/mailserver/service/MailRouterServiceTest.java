package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.AcceptedDomain;
import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.domain.Mailbox;
import com.vladimir.mailserver.domain.MailboxAlias;
import com.vladimir.mailserver.events.NewMailEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailRouterService.class)
public class MailRouterServiceTest {
    private MailRouterService mrs;

    @MockBean
    private OrgSettingsService orgSettingsService;

    @MockBean
    private MailService mailService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private AttachmentService attachmentService;

    @Before
    public void init() {
        mrs = new MailRouterService(orgSettingsService, mailService, applicationEventPublisher, attachmentService);
    }

    @Test
    public void testProcessMailSingleRecipientSuccess() {
        NewMailEvent event = new NewMailEvent(this, "from@localhost", "to@localhost", "subject", "body");
        final Mail[] mail = new Mail[1];
        Mockito.doAnswer(o -> mail[0] = o.getArgument(0)).when(mailService).save(Mockito.any());
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> aliasSet = new HashSet<>();
        Mailbox mailbox = new Mailbox();
        aliasSet.add(new MailboxAlias("to@localhost", mailbox, true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(aliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService).save(ArgumentMatchers.any(Mail.class));
        Mockito.verify(attachmentService, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(applicationEventPublisher, Mockito.never()).publishEvent(ArgumentMatchers.any());
        Assert.assertEquals(MailType.INCOMING, mail[0].getType());
        Assert.assertEquals(mailbox, mail[0].getMailbox());
        Assert.assertEquals("from@localhost", mail[0].getFrom());
        Assert.assertEquals("to@localhost", mail[0].getTo());
        Assert.assertEquals("subject", mail[0].getSubject());
        Assert.assertEquals("body", mail[0].getMessage());
        Assert.assertNotNull(mail[0].getDate());
    }

    @Test
    public void testProcessMailMultipleRecipientSuccess() {
        NewMailEvent event = new NewMailEvent(this, "from@localhost", "to@localhost, to2@localhost, to3@localhost", "subject", "body");
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> mailboxAliasSet = new HashSet<>();
        mailboxAliasSet.add(new MailboxAlias("to@localhost", new Mailbox(), true, null));
        mailboxAliasSet.add(new MailboxAlias("to2@localhost", new Mailbox(), true, null));
        mailboxAliasSet.add(new MailboxAlias("to3@localhost", new Mailbox(), true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(mailboxAliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService, Mockito.times(3)).save(ArgumentMatchers.any(Mail.class));
        Mockito.verify(attachmentService, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(applicationEventPublisher, Mockito.never()).publishEvent(ArgumentMatchers.any());
    }

    @Test
    public void testProcessMailAttachmentSuccess() {
        List<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(new Attachment("name", 2L, null, "body".getBytes()));
        NewMailEvent event = new NewMailEvent(this, "from@localhost", "to@localhost", "subject",
                "body", attachmentList);
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> mailboxAliasSet = new HashSet<>();
        Mailbox mailbox = new Mailbox();
        mailboxAliasSet.add(new MailboxAlias("to@localhost", mailbox, true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(mailboxAliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService).save(ArgumentMatchers.any(Mail.class));
        Mockito.verify(attachmentService).save(ArgumentMatchers.any(), ArgumentMatchers.eq(attachmentList));
        Mockito.verify(applicationEventPublisher, Mockito.never()).publishEvent(ArgumentMatchers.any());
    }

    @Test
    public void testProcessMailExternalRecipientsSuccess() {
        NewMailEvent event = new NewMailEvent(this, "from@localhost", "to@exthost", "subject",
                "body");
        final NewMailEvent[] newEvent = new NewMailEvent[1];
        Mockito.doAnswer(o -> newEvent[0] = o.getArgument(0)).when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> mailboxAliasSet = new HashSet<>();
        Mailbox mailbox = new Mailbox();
        mailboxAliasSet.add(new MailboxAlias("to@localhost", mailbox, true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(mailboxAliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService, Mockito.never()).save(ArgumentMatchers.any(Mail.class));
        Mockito.verify(attachmentService, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(applicationEventPublisher).publishEvent(ArgumentMatchers.any(NewMailEvent.class));
        Assert.assertEquals("from@localhost", newEvent[0].getTo());
        Assert.assertEquals("system@localhost", newEvent[0].getFrom());
        Assert.assertEquals("Mail delivery report.", newEvent[0].getSubject());
        Assert.assertTrue(newEvent[0].getBody().contains("External mails is not supported"));
    }

    @Test
    public void testProcessMailMiscRecipientsSuccess() {
        NewMailEvent event = new NewMailEvent(this, "from@localhost", "to@localhost, to2@externhost, to3@localhost", "subject", "body");
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> mailboxAliasSet = new HashSet<>();
        mailboxAliasSet.add(new MailboxAlias("to@localhost", new Mailbox(), true, null));
        mailboxAliasSet.add(new MailboxAlias("to2@localhost", new Mailbox(), true, null));
        mailboxAliasSet.add(new MailboxAlias("to3@localhost", new Mailbox(), true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(mailboxAliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService, Mockito.times(2)).save(ArgumentMatchers.any(Mail.class));
        Mockito.verify(attachmentService, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(applicationEventPublisher).publishEvent(ArgumentMatchers.any(NewMailEvent.class));
    }

    @Test
    public void testProcessMailUserNotFound() {
        NewMailEvent event = new NewMailEvent(this, "from@localhost", "to@localhost", "subject", "body");
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> mailboxAliasSet = new HashSet<>();
        mailboxAliasSet.add(new MailboxAlias("to2@localhost", new Mailbox(), true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(mailboxAliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService, Mockito.never()).save(ArgumentMatchers.any());
        Mockito.verify(attachmentService, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(applicationEventPublisher).publishEvent(ArgumentMatchers.any(NewMailEvent.class));
    }

    @Test
    public void testProcessMailFromExternalToInternalSuccess() {
        NewMailEvent event = new NewMailEvent(this, "from@exthost", "to@localhost", "subject", "body");
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> mailboxAliasSet = new HashSet<>();
        mailboxAliasSet.add(new MailboxAlias("to@localhost", new Mailbox(), true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(mailboxAliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService).save(ArgumentMatchers.any(Mail.class));
        Mockito.verify(attachmentService, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(applicationEventPublisher, Mockito.never()).publishEvent(ArgumentMatchers.any());
    }

    @Test
    public void testProcessMailFromExternalToExternalFail() {
        NewMailEvent event = new NewMailEvent(this, "from@exthost", "to@otherexthost", "subject", "body");
        Mockito.when(orgSettingsService.isDomainLocal("localhost")).thenReturn(true);
        Set<MailboxAlias> mailboxAliasSet = new HashSet<>();
        mailboxAliasSet.add(new MailboxAlias("to@localhost", new Mailbox(), true, null));
        AcceptedDomain domain = Mockito.mock(AcceptedDomain.class);
        Mockito.when(domain.getAliases()).thenReturn(mailboxAliasSet);
        Mockito.when(orgSettingsService.getDomainByName("localhost")).thenReturn(domain);
        mrs.processMail(event);
        Mockito.verify(mailService, Mockito.never()).save(ArgumentMatchers.any());
        Mockito.verify(attachmentService, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(applicationEventPublisher, Mockito.never()).publishEvent(ArgumentMatchers.any());
    }
}