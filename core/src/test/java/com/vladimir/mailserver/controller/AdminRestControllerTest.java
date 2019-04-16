package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.service.OrgSettingsService;
import com.vladimir.mailserver.service.UserService;
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

    private Pageable pageable = Pageable.unpaged();

    @Test
    public void testGetSettingsSuccess() {
        arc.getSettings();
        Mockito.verify(orgSettingsService, Mockito.times(1)).getOrgSettings();
    }

    @Test
    public void testSetOrgNameSuccess() {
        arc.setOrgName("name");
        Mockito.verify(orgSettingsService, Mockito.times(1)).setName(ArgumentMatchers.eq("name"));
    }

    @Test
    public void testAddNewDomainSuccess() {
        arc.addNewDomain("name");
        Mockito.verify(orgSettingsService, Mockito.times(1)).addDomain(ArgumentMatchers.eq("name"));
    }

    @Test
    public void testDeleteDomainSuccess() {
        arc.deleteDomain(2L);
        Mockito.verify(orgSettingsService, Mockito.times(1)).deleteDomain(ArgumentMatchers.eq(2L));
    }

    @Test
    public void testEditDomainSuccess() {
        arc.editDomain(2L, "name", true);
        Mockito.verify(orgSettingsService, Mockito.times(1)).editDomain(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("name"), ArgumentMatchers.eq(true));
    }

    @Test
    public void testGetUsersSuccess() {
        arc.getUsers(pageable);
        Mockito.verify(userService, Mockito.times(1)).getUsers(pageable);
    }

    @Test
    public void testEditUserSuccess() {
        arc.editUser(2L, "name", "surname", UserRoles.USER, UserStatus.DISABLED, "password");
        Mockito.verify(userService, Mockito.times(1)).editUser(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq(UserRoles.USER), ArgumentMatchers.eq(UserStatus.DISABLED),
                ArgumentMatchers.eq("password"));
    }

    @Test
    public void testGetAliasesSuccess() {
        arc.getAliases(2L);
        Mockito.verify(userService, Mockito.times(1)).getAliases(ArgumentMatchers.eq(2L));
    }

    @Test
    public void testAddAliasSuccess() {
        arc.addAlias(2L, "value");
        Mockito.verify(userService, Mockito.times(1)).addAlias(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("value"));
    }

    @Test
    public void testDeleteAliasSuccess() {
        arc.deleteAlias(2L);
        Mockito.verify(userService, Mockito.times(1)).deleteAlias(ArgumentMatchers.eq(2L));
    }

    @Test
    public void testEditAliasSuccess() {
        arc.editAlias(2L, "value", false);
        Mockito.verify(userService, Mockito.times(1)).editAlias(ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("value"), ArgumentMatchers.eq(false));
    }
}