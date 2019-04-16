package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.AcceptedDomain;
import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.domain.Mailbox;
import com.vladimir.mailserver.domain.MailboxAlias;
import com.vladimir.mailserver.events.NewMailEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class MailRouterService {
    private OrgSettingsService orgSettingsService;
    private MailService mailService;
    private ApplicationEventPublisher applicationEventPublisher;
    private AttachmentService attachmentService;

    public MailRouterService(OrgSettingsService orgSettingsService, MailService mailService,
                             ApplicationEventPublisher applicationEventPublisher, AttachmentService attachmentService) {
        this.orgSettingsService = orgSettingsService;
        this.mailService = mailService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.attachmentService = attachmentService;
    }

    @Async
    @EventListener
    public void processMail(NewMailEvent event) {
        String[] addresses = parseAddresses(event.getTo());
        boolean internalSender = isLocal(event.getFrom());
        for (String address : addresses) {
            if (internalSender) {
                if (isLocal(address)) {
                    toInternal(event, address);
                } else {
                    toExternal(event, address);
                }
            } else {
                if (isLocal(address)) {
                    toInternal(event, address);
                }
            }
        }
    }

    private boolean isLocal(String address) {
        String domain = address.substring(address.indexOf('@') + 1);
        return orgSettingsService.isDomainLocal(domain);
    }

    private String[] parseAddresses(String address) {
        String[] addresses = address.split(",");
        addresses = Arrays.stream(addresses).map(String::trim).toArray(String[]::new);
        return addresses;
    }

    private void toInternal(NewMailEvent event, String address) {
        String domainName = address.substring(address.indexOf('@') + 1);
        AcceptedDomain domain = orgSettingsService.getDomainByName(domainName);
        Mailbox mailbox = null;
        for (MailboxAlias alias : domain.getAliases()) {
            if (alias.getValue().equals(address.toLowerCase())) {
                mailbox = alias.getMailbox();
                break;
            }
        }
        if (mailbox != null) {
            Mail mail = new Mail(MailType.INCOMING, mailbox, event.getFrom(), event.getTo(), event.getSubject(),
                    event.getBody(), new Date());
            mailService.save(mail);
            if (event.getAttachments() != null) {
                attachmentService.save(mail, event.getAttachments());
            }
        } else {
            String from = "system" + address.substring(address.indexOf('@'));
            String subject = "Mail delivery report.";
            String body = "Mail delivery error: user not found.\n" +
                    "To: " + address + "\n" +
                    "Subject: " + event.getSubject();
            NewMailEvent ndrEvent = new NewMailEvent(this, from, event.getFrom(), subject, body);
            applicationEventPublisher.publishEvent(ndrEvent);
        }
    }

    private void toExternal(NewMailEvent event, String address) {
        String from = "system@localhost";
        String subject = "Mail delivery report.";
        String body = "Mail delivery error: External mails is not supported yet.\n" +
                "To: " + address + "\n" +
                "Subject: " + event.getSubject();
        NewMailEvent ndrEvent = new NewMailEvent(this, from, event.getFrom(), subject, body);
        applicationEventPublisher.publishEvent(ndrEvent);
    }
}
