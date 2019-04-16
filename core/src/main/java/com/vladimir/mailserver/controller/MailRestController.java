package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.dto.MailDto;
import com.vladimir.mailserver.service.MailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest/mail")
public class MailRestController {
    private MailService mailService;

    public MailRestController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ResponseEntity send(@RequestParam String to, @RequestParam String subject, @RequestParam String body,
                               @RequestParam List<MultipartFile> files) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        mailService.send(login, to.toLowerCase(), subject, body, files);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).build();
    }

    @GetMapping("/get/incoming")
    public Page<MailDto> getIncoming(Pageable pageable) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return mailService.getMails(login, MailType.INCOMING, pageable);
    }

    @GetMapping("/get/sent")
    public Page<MailDto> getSent(Pageable pageable) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return mailService.getMails(login, MailType.SENT, pageable);
    }

    @GetMapping("/get/deleted")
    public Page<MailDto> getDeleted(Pageable pageable) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return mailService.getDeletedMails(login, pageable);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        mailService.delete(login, id);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).build();
    }

    @GetMapping("/restore/{id}")
    public ResponseEntity restore(@PathVariable Long id) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        mailService.restore(login, id);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).build();
    }
}
