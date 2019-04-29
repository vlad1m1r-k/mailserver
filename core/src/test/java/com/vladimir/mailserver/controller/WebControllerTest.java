package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.Attachment;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.service.AttachmentService;
import com.vladimir.mailserver.service.UserService;
import com.vladimir.mailserver.service.ValidatorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebController.class)
public class WebControllerTest {
    @Autowired
    private WebController wc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private AttachmentService attachmentService;

    @MockBean
    private ValidatorService validatorService;

    @Mock
    private Model model;

    @Before
    public void setUp() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetIndexUserExist() {
        MailUser user = Mockito.mock(MailUser.class);
        Mockito.when(user.isEnabled()).thenReturn(true);
        Mockito.when(userService.find("username")).thenReturn(user);
        String result = wc.getIndex(model);
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("username"));
        Mockito.verify(SecurityContextHolder.getContext().getAuthentication(), Mockito.never()).setAuthenticated(false);
        Mockito.verify(userService, Mockito.times(1)).getUserDto(ArgumentMatchers.eq("username"));
        Mockito.verify(model, Mockito.times(1)).addAttribute(ArgumentMatchers.eq("user"),
                ArgumentMatchers.any());
        Assert.assertEquals("index", result);
    }

    @Test
    public void testGetIndexUserNotExist() {
        Mockito.when(userService.find("username")).thenReturn(null);
        String result = wc.getIndex(model);
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("username"));
        Mockito.verify(SecurityContextHolder.getContext().getAuthentication(), Mockito.atLeastOnce()).setAuthenticated(false);
        Mockito.verify(userService, Mockito.never()).getUserDto(ArgumentMatchers.any());
        Mockito.verify(model, Mockito.never()).addAttribute(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Assert.assertEquals("index", result);
    }

    @Test
    public void testGetIndexUserIsDisabled() {
        MailUser user = Mockito.mock(MailUser.class);
        Mockito.when(user.isEnabled()).thenReturn(false);
        Mockito.when(userService.find("username")).thenReturn(user);
        String result = wc.getIndex(model);
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("username"));
        Mockito.verify(SecurityContextHolder.getContext().getAuthentication(), Mockito.atLeastOnce()).setAuthenticated(false);
        Mockito.verify(userService, Mockito.never()).getUserDto(ArgumentMatchers.any());
        Mockito.verify(model, Mockito.never()).addAttribute(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Assert.assertEquals("index", result);
    }

    @Test
    public void testLoginSuccess() {
        Assert.assertEquals("login", wc.login());
    }

    @Test
    public void testRegisterFail() {
        MailUser user = Mockito.mock(MailUser.class);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString())).thenReturn(true);
        String result = wc.register(model, null, "name", "surname", "login", "password");
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq("login"), ArgumentMatchers.eq("password"));
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("login"));
        Mockito.verify(model, Mockito.atLeastOnce()).addAttribute(ArgumentMatchers.eq("taken"), ArgumentMatchers.eq(true));
        Mockito.verify(model, Mockito.atLeastOnce()).addAttribute(ArgumentMatchers.eq("register"), ArgumentMatchers.eq(true));
        Mockito.verify(model, Mockito.atLeastOnce()).addAttribute(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("name"));
        Mockito.verify(model, Mockito.atLeastOnce()).addAttribute(ArgumentMatchers.eq("surName"), ArgumentMatchers.eq("surname"));
        Mockito.verify(model, Mockito.atLeastOnce()).addAttribute(ArgumentMatchers.eq("login"), ArgumentMatchers.eq("login"));
        Assert.assertEquals("login", result);
        Mockito.verify(userService, Mockito.never()).createUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(authenticationManager, Mockito.never()).authenticate(ArgumentMatchers.any());
    }

    @Test
    public void testRegisterValidationFail() {
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString())).thenReturn(false);
        String result = wc.register(model, null, "name", "surname", "login", "password");
        Mockito.verify(validatorService).validateUserData(ArgumentMatchers.eq("name"), ArgumentMatchers.eq("surname"),
                ArgumentMatchers.eq("login"), ArgumentMatchers.eq("password"));
        Mockito.verify(userService, Mockito.never()).find(ArgumentMatchers.any());
        Mockito.verify(model, Mockito.never()).addAttribute(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(userService, Mockito.never()).createUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(authenticationManager, Mockito.never()).authenticate(ArgumentMatchers.any());
        Assert.assertNull(result);
    }

    @Test
    public void testRegisterSuccess() {
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(null);
        Mockito.when(validatorService.validateUserData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString())).thenReturn(true);
        String result = wc.register(model, Mockito.mock(HttpServletRequest.class), "name", "surname", "login", "password");
        Mockito.verify(userService).find(ArgumentMatchers.eq("login"));
        Mockito.verify(model, Mockito.never()).addAttribute(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertEquals("redirect:/", result);
        Mockito.verify(userService, Mockito.atLeastOnce()).createUser(ArgumentMatchers.eq("name"),
                ArgumentMatchers.eq("surname"), ArgumentMatchers.eq("login"),
                ArgumentMatchers.eq("password"));
        Mockito.verify(authenticationManager, Mockito.atLeastOnce()).authenticate(ArgumentMatchers.any());
    }

    @Test
    public void testGetAttachmentFail() {
        ResponseEntity<byte[]> result = wc.getAttachment(2L);
        Mockito.verify(attachmentService).getAttachment(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq(2L));
        Assert.assertEquals(new ResponseEntity<>("Resource Not found".getBytes(), HttpStatus.NOT_FOUND), result);
    }

    @Test
    public void testGetAttachmentSuccess() {
        Attachment attachment = Mockito.mock(Attachment.class);
        Mockito.when(attachment.getName()).thenReturn("name");
        Mockito.when(attachment.getSize()).thenReturn(12L);
        Mockito.when(attachment.getBody()).thenReturn("body".getBytes());
        Mockito.when(attachment.getType()).thenReturn("application/pdf");
        Mockito.when(attachmentService.getAttachment(Mockito.anyString(), Mockito.anyLong())).thenReturn(attachment);
        ResponseEntity<byte[]> result = wc.getAttachment(2L);
        Assert.assertEquals(MediaType.APPLICATION_PDF, result.getHeaders().getContentType());
        Assert.assertEquals("attachment; filename=name", result.getHeaders().getFirst("Content-disposition"));
        Assert.assertEquals("12", result.getHeaders().getFirst("Content-length"));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}