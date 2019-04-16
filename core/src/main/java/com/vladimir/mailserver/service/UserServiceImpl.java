package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.AcceptedDomain;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.Mailbox;
import com.vladimir.mailserver.domain.MailboxAlias;
import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.dto.MailUserDto;
import com.vladimir.mailserver.dto.MailboxAliasDto;
import com.vladimir.mailserver.repository.MailAliasRepository;
import com.vladimir.mailserver.repository.MailboxRepository;
import com.vladimir.mailserver.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private OrgSettingsService orgSettingsService;
    private MailboxRepository mailboxRepository;
    private MailAliasRepository aliasRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, OrgSettingsService orgSettingsService,
                           MailboxRepository mailboxRepository, MailAliasRepository aliasRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orgSettingsService = orgSettingsService;
        this.mailboxRepository = mailboxRepository;
        this.aliasRepository = aliasRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createUser(String name, String surname, String login, String password) {
        MailUser user = new MailUser(name, surname, login, passwordEncoder.encode(password), UserRoles.USER, UserStatus.ACTIVE);
        user = userRepository.save(user);
        Mailbox mailbox = mailboxRepository.save(new Mailbox(user));
        AcceptedDomain defaultDomain = orgSettingsService.getDefaultDomain();
        String aliasValue = login + "@" + defaultDomain.getName();
        MailboxAlias alias = new MailboxAlias(aliasValue, mailbox, true, defaultDomain);
        aliasRepository.save(alias);
    }

    @Override
    @Transactional(readOnly = true)
    public MailUser find(String login) {
        return userRepository.findMailUserByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public MailUserDto getUserDto(String login) {
        MailUser user = find(login);
        return new MailUserDto.Builder()
                .name(user.getName())
                .surname(user.getSurname())
                .login(user.getLogin())
                .role(user.getRole())
                .defaultAlias(user.getMailbox().getDefaultAlias().getValue())
                .build();
    }

    @Override
    @Transactional
    public boolean updateUser(String login, String name, String surName, String password, String newPassword) {
        MailUser user = find(login);
        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setName(name);
            user.setSurname(surName);
            if (newPassword != null && newPassword.length() > 2) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MailUserDto> getUsers(Pageable pageable) {
        Page<MailUser> users = userRepository.findAll(pageable);
        return users.map(user -> new MailUserDto.Builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .login(user.getLogin())
                .role(user.getRole())
                .status(user.getStatus())
                .build());
    }

    @Override
    @Transactional
    public void editUser(Long id, String name, String surname, UserRoles role, UserStatus status, String password) {
        MailUser user = userRepository.getOne(id);
        if (user != null) {
            user.setName(name);
            user.setSurname(surname);
            user.setRole(role);
            user.setStatus(status);
            if (!password.equals("")) {
                user.setPassword(passwordEncoder.encode(password));
            }
            userRepository.save(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MailboxAliasDto> getAliases(Long userId) {
        MailUser user = userRepository.getOne(userId);
        return user.getMailbox().getAliases().stream().map(als -> new MailboxAliasDto.Builder()
                .id(als.getId())
                .name(als.getValue())
                .isDefault(als.isDefault())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addAlias(Long userId, String value) {
        Mailbox mailbox = userRepository.getOne(userId).getMailbox();
        String domainName = value.substring(value.indexOf('@') + 1);
        AcceptedDomain domain = orgSettingsService.getDomainByName(domainName);
        MailboxAlias alias = new MailboxAlias(value.toLowerCase(), mailbox, false, domain);
        aliasRepository.save(alias);
    }

    @Override
    @Transactional
    public boolean deleteAlias(Long id) {
        MailboxAlias alias = aliasRepository.getOne(id);
        if (!alias.isDefault()) {
            aliasRepository.delete(alias);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void editAlias(Long id, String value, Boolean doDefault) {
        MailboxAlias alias;
        if (doDefault) {
            alias = aliasRepository.getOne(id);
            Mailbox mailbox = alias.getMailbox();
            alias = mailbox.getDefaultAlias();
            alias.setDefault(false);
            aliasRepository.save(alias);
        }
        alias = aliasRepository.getOne(id);
        if (doDefault) {
            alias.setDefault(true);
        }
        alias.setValue(value.toLowerCase());
        String domainName = value.substring(value.indexOf('@') + 1);
        AcceptedDomain domain = orgSettingsService.getDomainByName(domainName);
        alias.setDomain(domain);
        aliasRepository.save(alias);
    }
}
