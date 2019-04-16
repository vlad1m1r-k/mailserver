package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.service.AttachmentService;
import com.vladimir.mailserver.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebController {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private AttachmentService attachmentService;

    public WebController(AuthenticationManager authenticationManager, UserService mailUserService, AttachmentService attachmentService) {
        this.authenticationManager = authenticationManager;
        this.userService = mailUserService;
        this.attachmentService = attachmentService;
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        MailUser user = userService.find(login);
        if (user == null || !user.isEnabled()) {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
            return "index";
        }
        model.addAttribute("user", userService.getUserDto(login));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/register")
    public String register(Model model, HttpServletRequest request, @RequestParam String name, @RequestParam String surName,
                           @RequestParam String login, @RequestParam String password1) {
        MailUser user = userService.find(login);
        if (user != null) {
            model.addAttribute("taken", true);
            model.addAttribute("register", true);
            model.addAttribute("name", name);
            model.addAttribute("surName", surName);
            model.addAttribute("login", login);
            return "login";
        }
        userService.createUser(name, surName, login, password1);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password1);
        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/";
    }

    @GetMapping("/attachment/{id}")
    public ResponseEntity<byte[]> getAttachment(@PathVariable Long id) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Attachment attachment = attachmentService.getAttachment(login, id);
        if (attachment == null) {
            return new ResponseEntity<>("Resource Not found".getBytes(), HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(attachment.getType()));
        headers.set("Content-disposition", "attachment; filename=" + attachment.getName());
        headers.set("Content-length", attachment.getSize().toString());
        return new ResponseEntity<>(attachment.getBody(), headers, HttpStatus.OK);
    }
}
