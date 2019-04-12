package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.dto.MailUserDto;
import com.vladimir.mailserver.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/user")
public class UserRestController {
    private UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get")
    public MailUserDto getUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserDto(login);
    }

    @PostMapping("/update")
    public boolean updateUser(@RequestParam String name, @RequestParam String surName, @RequestParam String password,
                              @RequestParam String newPass) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.updateUser(login, name, surName, password, newPass);
    }
}
