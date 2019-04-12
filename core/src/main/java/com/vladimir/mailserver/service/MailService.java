package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.dto.MailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MailService {
    void send(String login, String to, String subject, String body, List<MultipartFile> files);
    void save(Mail mail);
    Page<MailDto> getMails(String login, MailType type, Pageable pageable);
    Page<MailDto> getDeletedMails(String login, Pageable pageable);
    void delete(String login, Long id);
    void restore(String login, Long id);
}
