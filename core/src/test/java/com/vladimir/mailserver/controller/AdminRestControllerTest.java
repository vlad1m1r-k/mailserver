package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.service.OrgSettingsService;
import com.vladimir.mailserver.service.UserService;
import com.vladimir.mailserver.service.ValidatorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminRestController.class)
public class AdminRestControllerTest {
    @Autowired
    private AdminRestController arc;

    @MockBean
    private OrgSettingsService orgSettingsService;

    @MockBean
    private UserService userService;

    @MockBean
    private ValidatorService validatorService;

    private Pageable pageable = Pageable.unpaged();

    @Test
    public void testGetSettingsSuccess() {
        arc.getSettings();
        Mockito.verify(orgSettingsService).getOrgSettings();
    }

    @Test
    public void testSetOrgNameSuccess() {
        Mockito.when(validatorService.validateOrgName(Mockito.anyString())).thenReturn(true);
        arc.setOrgName("name");
        Mockito.verify(validatorService).validateOrgName(ArgumentMatchers.eq("name"));
        Mockito.verify(orgSettingsService).setName(ArgumentMatchers.eq("name"));
    }

    @Test
    public void testSetOrgNameValidationFail() {
        Mockito.when(validatorService.validateOrgName(Mockito.anyString())).thenReturn(false);
        arc.setOrgName("name");
        Mockito.verify(orgSettingsService, Mockito.never()).setName(ArgumentMatchers.any());
    }

    @Test
    public void testAddNewDomainSuccess() {
        Mockito.when(validatorService.validateOrgName(Mockito.anyString())).thenReturn(true);
        arc.addNewDomain("name");
        Mockito.verify(validatorService).validateOrgName(ArgumentMatchers.eq("name"));
        Mockito.verify(orgSettingsService).addDomain(ArgumentMatchers.eq("name"));
    }

    @Test
    public void testAddNewDomainValidationFail() {
        Mockito.when(validatorService.validateOrgName(Mockito.anyString())).thenReturn(false);
        arc.setOrgName("name");
        Mockito.verify(orgSettingsService, Mockito.never()).addDomain(ArgumentMatchers.any());
    }

    @Test
    public void testDeleteDomainSuccess() {
        arc.deleteDomain(2L);
        Mockito.verify(orgSettingsService).deleteDomain(ArgumentMatchers.eq(2L));
    }

    @Test
    public void testEditDomainSuccess() {
        Mockito.when(validatorService.validateOrgName(Mockito.anyString())).thenReturn(true);
        arc.editDomain(2L, "name", true);
        Mockito.verify(validatorService).validateOrgName(ArgumentMatchers.eq("name"));
        Mockito.verify(orgSettingsService).editDomain(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("name"), ArgumentMatchers.eq(true));
    }

    @Test
    public void testEditDomainValidationFail() {
        Mockito.when(validatorService.validateOrgName(Mockito.anyString())).thenReturn(false);
        arc.editDomain(2L, "name", true);
        Mockito.verify(orgSettingsService, Mockito.never()).editDomain(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testGetUsersSuccess() {
        arc.getUsers(pageable);
        Mockito.verify(userService).getUsers(pageable);
    }

    @Test
    public void testEditUserSuccess() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);
        arc.editUser(2L, "name", "surname", UserRoles.USER, UserStatus.DISABLED, "password");
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq("password"));
        Mockito.verify(userService).editUser(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq(UserRoles.USER), ArgumentMatchers.eq(UserStatus.DISABLED),
                ArgumentMatchers.eq("password"));
    }

    @Test
    public void testEditUserNoPasswordChange() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);
        arc.editUser(2L, "name", "surname", UserRoles.USER, UserStatus.DISABLED, "");
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"));
        Mockito.verify(userService).editUser(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq(UserRoles.USER), ArgumentMatchers.eq(UserStatus.DISABLED),
                ArgumentMatchers.eq(""));
    }

    @Test
    public void testEditUserValidationFail() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(false);
        arc.editUser(2L, "name", "surname", UserRoles.USER, UserStatus.DISABLED, "password");
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq("password"));
        Mockito.verify(userService, Mockito.never()).editUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testGetAliasesSuccess() {
        arc.getAliases(2L);
        Mockito.verify(userService).getAliases(ArgumentMatchers.eq(2L));
    }

    @Test
    public void testAddAliasSuccess() {
        Mockito.when(validatorService.validateAliasValue(Mockito.anyString())).thenReturn(true);
        arc.addAlias(2L, "value");
        Mockito.verify(validatorService).validateAliasValue(ArgumentMatchers.eq("value"));
        Mockito.verify(userService).addAlias(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("value"));
    }

    @Test
    public void testAddAliasValidationFail() {
        Mockito.when(validatorService.validateAliasValue(Mockito.anyString())).thenReturn(false);
        arc.addAlias(2L, "value");
        Mockito.verify(validatorService).validateAliasValue(ArgumentMatchers.eq("value"));
        Mockito.verify(userService, Mockito.never()).addAlias(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testDeleteAliasSuccess() {
        arc.deleteAlias(2L);
        Mockito.verify(userService).deleteAlias(ArgumentMatchers.eq(2L));
    }

    @Test
    public void testEditAliasSuccess() {
        Mockito.when(validatorService.validateAliasValue(Mockito.anyString())).thenReturn(true);
        arc.editAlias(2L, "value", false);
        Mockito.verify(validatorService).validateAliasValue(ArgumentMatchers.eq("value"));
        Mockito.verify(userService).editAlias(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("value"), ArgumentMatchers.eq(false));
    }

    @Test
    public void testEditAliasValidationFail() {
        Mockito.when(validatorService.validateAliasValue(Mockito.anyString())).thenReturn(false);
        arc.editAlias(2L, "value", false);
        Mockito.verify(validatorService).validateAliasValue(ArgumentMatchers.eq("value"));
        Mockito.verify(userService, Mockito.never()).editAlias(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }
}