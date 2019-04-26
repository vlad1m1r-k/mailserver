package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.service.UserService;
import com.vladimir.mailserver.service.ValidatorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserRestController.class)
public class UserRestControllerTest {
    @Autowired
    private UserRestController urc;

    @MockBean
    private UserService userService;

    @MockBean
    private ValidatorService validatorService;

    @Before
    public void init() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetUserSuccess() {
        urc.getUser();
        Mockito.verify(userService, Mockito.times(1)).getUserDto(ArgumentMatchers.eq("username"));
    }

    @Test
    public void testUpdateUserSuccess() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        urc.updateUser("name", "surname", "password", "newpassword");
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq("newpassword"));
        Mockito.verify(userService, Mockito.times(1)).updateUser(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"), ArgumentMatchers.eq("password"),
                ArgumentMatchers.eq("newpassword"));
    }

    @Test
    public void testUpdateUserNoPassworsChange() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        urc.updateUser("name", "surname", "password", null);
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"));
    }

    @Test
    public void testUpdateUserWithPassworsChange() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        urc.updateUser("name", "surname", "password", "newpassword");
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq("newpassword"));
    }

    @Test
    public void testUpdateUserValidationFail() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        urc.updateUser("name", "surname", "password", null);
        Mockito.verify(userService, Mockito.never()).updateUser(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}