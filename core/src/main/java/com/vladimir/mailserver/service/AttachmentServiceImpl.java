package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    @Transactional
    public void save(Mail mail, List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            Attachment attach = new Attachment(attachment.getName(), attachment.getSize(), attachment.getType(), attachment.getBody());
            attach.setMail(mail);
            attachmentRepository.save(attach);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Attachment getAttachment(String login, Long id) {
        Attachment attachment = attachmentRepository.findAttachmentById(id);
        String attachLogin = attachment.getMail().getMailbox().getUser().getLogin();
        if (login.equals(attachLogin)) {
            return attachment;
        }
        return null;
    }
}
