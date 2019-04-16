package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.dto.MailUserDto;
import com.vladimir.mailserver.dto.MailboxAliasDto;
import com.vladimir.mailserver.dto.OrgSettingsDto;
import com.vladimir.mailserver.service.OrgSettingsService;
import com.vladimir.mailserver.service.UserService;
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

    public AdminRestController(OrgSettingsService orgSettingsService, UserService userService) {
        this.orgSettingsService = orgSettingsService;
        this.userService = userService;
    }

    @GetMapping("/settings")
    public OrgSettingsDto getSettings() {
        return orgSettingsService.getOrgSettings();
    }

    @GetMapping("/org/name/{name}")
    public boolean setOrgName(@PathVariable String name) {
        orgSettingsService.setName(name);
        return true;
    }

    @GetMapping("/domain/new/{name}")
    public boolean addNewDomain(@PathVariable String name) {
        orgSettingsService.addDomain(name);
        return true;
    }

    @GetMapping("/domain/del/{id}")
    public boolean deleteDomain(@PathVariable Long id) {
        return orgSettingsService.deleteDomain(id);
    }

    @GetMapping("/domain/edit/{id}/{name}/{toDefault}")
    public boolean editDomain(@PathVariable Long id, @PathVariable String name, @PathVariable Boolean toDefault) {
        orgSettingsService.editDomain(id, name, toDefault);
        return true;
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
        userService.editUser(id, name, surname, role, status, password);
        return true;
    }

    @GetMapping("/user/aliases/{userId}")
    public List<MailboxAliasDto> getAliases(@PathVariable Long userId) {
        return userService.getAliases(userId);
    }

    @GetMapping("/user/{userId}/alias/add/{value}")
    public boolean addAlias(@PathVariable Long userId, @PathVariable String value) {
        userService.addAlias(userId, value);
        return true;
    }

    @GetMapping("/user/alias/delete/{id}")
    public boolean deleteAlias(@PathVariable Long id) {
        return userService.deleteAlias(id);
    }

    @GetMapping("/alias/edit/{id}/{value}/{doDefault}")
    public boolean editAlias(@PathVariable Long id, @PathVariable String value, @PathVariable Boolean doDefault) {
        userService.editAlias(id, value, doDefault);
        return true;
    }
}
