package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.service.UserService;
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
        urc.updateUser("name", "surname", "password", "newpassword");
        Mockito.verify(userService, Mockito.times(1)).updateUser(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"), ArgumentMatchers.eq("password"),
                ArgumentMatchers.eq("newpassword"));
    }
}