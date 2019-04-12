package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.Mail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AttachmentService {
    void save(Mail mail, List<Attachment> attachments);
    Attachment getAttachment(String login, Long id);
}
