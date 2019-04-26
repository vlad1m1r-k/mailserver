package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.dto.MailUserDto;
import com.vladimir.mailserver.dto.MailboxAliasDto;
import com.vladimir.mailserver.dto.OrgSettingsDto;
import com.vladimir.mailserver.service.OrgSettingsService;
import com.vladimir.mailserver.service.UserService;
import com.vladimir.mailserver.service.ValidatorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRestController {
    private OrgSettingsService orgSettingsService;
    private UserService userService;
    private ValidatorService validatorService;

    public AdminRestController(OrgSettingsService orgSettingsService, UserService userService, ValidatorService validatorService) {
        this.orgSettingsService = orgSettingsService;
        this.userService = userService;
        this.validatorService = validatorService;
    }

    @GetMapping("/settings")
    public OrgSettingsDto getSettings() {
        return orgSettingsService.getOrgSettings();
    }

    @GetMapping("/org/name/{name}")
    public boolean setOrgName(@PathVariable String name) {
        if (validatorService.validateOrgName(name)) {
            orgSettingsService.setName(name);
            return true;
        }
        return false;
    }

    @GetMapping("/domain/new/{name}")
    public boolean addNewDomain(@PathVariable String name) {
        if (validatorService.validateOrgName(name)) {
            orgSettingsService.addDomain(name);
            return true;
        }
        return false;
    }

    @GetMapping("/domain/del/{id}")
    public boolean deleteDomain(@PathVariable Long id) {
        if (id != null && id > 0) {
            return orgSettingsService.deleteDomain(id);
        }
        return false;
    }

    @GetMapping("/domain/edit/{id}/{name}/{toDefault}")
    public boolean editDomain(@PathVariable Long id, @PathVariable String name, @PathVariable Boolean toDefault) {
        if (id != null && id > 0 && toDefault != null && validatorService.validateOrgName(name)) {
            orgSettingsService.editDomain(id, name, toDefault);
            return true;
        }
        return false;
    }

    @GetMapping("/roles")
    public UserRoles[] getUserRoles() {
        return UserRoles.values();
    }

    @GetMapping("/status")
    public UserStatus[] getUserStatus() {
        return UserStatus.values();
    }

    @GetMapping("/users/get")
    public Page<MailUserDto> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @PostMapping("/user/edit")
    public boolean editUser(@RequestParam Long id, @RequestParam String name, @RequestParam String surname,
                            @RequestParam UserRoles role, @RequestParam UserStatus status,
                            @RequestParam(required = false) String password) {
        if (id != null && id >0 && role != null && status != null && (password.equals("") &&
                validatorService.validateUserData(name, surname)) || validatorService.validateUserData(name, surname, password)) {
            userService.editUser(id, name, surname, role, status, password);
            return true;
        }
        return false;
    }

    @GetMapping("/user/aliases/{userId}")
    public List<MailboxAliasDto> getAliases(@PathVariable Long userId) {
        if (userId != null && userId > 0) {
            return userService.getAliases(userId);
        }
        return null;
    }

    @GetMapping("/user/{userId}/alias/add/{value}")
    public boolean addAlias(@PathVariable Long userId, @PathVariable String value) {
        if(userId != null && userId > 0 && validatorService.validateAliasValue(value)) {
            userService.addAlias(userId, value);
            return true;
        }
        return false;
    }

    @GetMapping("/user/alias/delete/{id}")
    public boolean deleteAlias(@PathVariable Long id) {
        if (id != null && id > 0) {
            return userService.deleteAlias(id);
        }
        return false;
    }

    @GetMapping("/alias/edit/{id}/{value}/{doDefault}")
    public boolean editAlias(@PathVariable Long id, @PathVariable String value, @PathVariable Boolean doDefault) {
        if (id != null && id > 0 && doDefault != null && validatorService.validateAliasValue(value)) {
            userService.editAlias(id, value, doDefault);
            return true;
        }
        return false;
    }
}
