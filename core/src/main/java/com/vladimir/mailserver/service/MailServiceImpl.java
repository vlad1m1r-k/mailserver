package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.Mailbox;
import com.vladimir.mailserver.dto.AttachmentDto;
import com.vladimir.mailserver.dto.MailDto;
import com.vladimir.mailserver.events.NewMailEvent;
import com.vladimir.mailserver.repository.MailRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailServiceImpl implements MailService {
    private UserService userService;
    private MailRepository mailRepository;
    private AttachmentService attachmentService;
    private ApplicationEventPublisher applicationEventPublisher;

    public MailServiceImpl(UserService userService, MailRepository mailRepository,
                           ApplicationEventPublisher applicationEventPublisher, AttachmentService attachmentService) {
        this.userService = userService;
        this.mailRepository = mailRepository;
        this.attachmentService = attachmentService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public void send(String login, String to, String subject, String body, List<MultipartFile> files) {
        MailUser user = userService.find(login);
        Mailbox mailbox = user.getMailbox();
        String from = mailbox.getDefaultAlias().getValue();
        Mail mail = new Mail(MailType.SENT, mailbox, from, to, subject, body, new Date());
        mailRepository.save(mail);
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                attachments.add(new Attachment(file.getOriginalFilename(), file.getSize(), file.getContentType(), file.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        attachmentService.save(mail, attachments);
        applicationEventPublisher.publishEvent(new NewMailEvent(this, from, to, subject, body, attachments));
    }

    @Override
    @Transactional
    public void save(Mail mail) {
        mailRepository.save(mail);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MailDto> getMails(String login, MailType type, Pageable pageable) {
        MailUser user = userService.find(login);
        Mailbox mailbox = user.getMailbox();
        return dtoWrap(mailRepository.findAllByMailboxAndTypeAndIsDeleted(mailbox, type, false, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MailDto> getDeletedMails(String login, Pageable pageable) {
        MailUser user = userService.find(login);
        Mailbox mailbox = user.getMailbox();
        return dtoWrap(mailRepository.findDeleted(mailbox, false, MailType.DELETED_INC,
                MailType.DELETED_SENT, pageable));
    }

    @Override
    @Transactional
    public void delete(String login, Long id) {
        MailUser user = userService.find(login);
        Mailbox mailbox = user.getMailbox();
        Mail mail = mailRepository.findMailById(id);
        if (mailbox.equals(mail.getMailbox())) {
            switch (mail.getType()) {
                case INCOMING:
                    mail.setType(MailType.DELETED_INC);
                    break;
                case SENT:
                    mail.setType(MailType.DELETED_SENT);
                    break;
                case DELETED_INC:
                case DELETED_SENT:
                    mail.setDeleted(true);
            }
            mailRepository.save(mail);
        }
    }

    @Override
    @Transactional
    public void restore(String login, Long id) {
        MailUser user = userService.find(login);
        Mailbox mailbox = user.getMailbox();
        Mail mail = mailRepository.findMailById(id);
        if (mailbox.equals(mail.getMailbox())) {
            switch (mail.getType()) {
                case DELETED_INC:
                    mail.setType(MailType.INCOMING);
                    break;
                case DELETED_SENT:
                    mail.setType(MailType.SENT);
            }
            mailRepository.save(mail);
        }
    }

    private Page<MailDto> dtoWrap(Page<Mail> mailPage) {
        return mailPage.map(mail -> {
            List<AttachmentDto> attDtos = mail.getAttachments().stream().map(att -> AttachmentDto.builder()
                    .id(att.getId())
                    .name(att.getName())
                    .size(att.getSize())
                    .build())
                    .collect(Collectors.toList());
            return MailDto.builder()
                    .id(mail.getId())
                    .from(mail.getFrom())
                    .to(mail.getTo())
                    .subject(mail.getSubject())
                    .message(mail.getMessage())
                    .date(mail.getDate())
                    .attachments(attDtos)
                    .build();
        });
    }
}
