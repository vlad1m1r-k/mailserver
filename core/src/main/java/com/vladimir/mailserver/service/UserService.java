package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.dto.MailUserDto;
import com.vladimir.mailserver.dto.MailboxAliasDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    void createUser(String name, String surname, String login, String password);
    MailUser find(String login);
    MailUserDto getUserDto(String login);
    boolean updateUser(String login, String name, String surName, String password, String newPassword);
    Page<MailUserDto> getUsers(Pageable pageable);
    void editUser(Long id, String name, String surname, UserRoles role, UserStatus status, String password);
    List<MailboxAliasDto> getAliases(Long userId);
    void addAlias(Long userId, String value);
    boolean deleteAlias(Long id);
    void editAlias(Long id, String value, Boolean doDefault);
}
