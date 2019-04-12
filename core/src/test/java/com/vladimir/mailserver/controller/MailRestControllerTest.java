package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.service.MailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailRestController.class)
public class MailRestControllerTest {
    @Autowired
    private MailRestController mrc;

    @MockBean
    private MailService mailService;
    private Pageable pageable = Pageable.unpaged();
    private List<MultipartFile> attachment = new ArrayList<>();

    @Before
    public void setUp() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testSendSuccess() {
        mrc.send("sendto", "subject", "body", attachment);
        Mockito.verify(mailService, Mockito.times(1)).send(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq("sendto"), ArgumentMatchers.eq("subject"), ArgumentMatchers.eq("body"),
                ArgumentMatchers.eq(attachment));
    }

    @Test
    public void testGetIncomingSuccess() {
        mrc.getIncoming(pageable);
        Mockito.verify(mailService, Mockito.times(1)).getMails(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq(MailType.INCOMING), ArgumentMatchers.eq(pageable));
    }

    @Test
    public void testGetSentSuccess() {
        mrc.getSent(pageable);
        Mockito.verify(mailService, Mockito.times(1)).getMails(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq(MailType.SENT), ArgumentMatchers.eq(pageable));
    }

    @Test
    public void testGetDeletedSuccess() {
        mrc.getDeleted(pageable);
        Mockito.verify(mailService, Mockito.times(1)).getDeletedMails(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq(pageable));
    }

    @Test
    public void testDeleteSuccess() {
        mrc.delete(2L);
        Mockito.verify(mailService, Mockito.times(1)).delete(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq(2L));
    }

    @Test
    public void testRestoreSuccess() {
        mrc.restore(2L);
        Mockito.verify(mailService, Mockito.times(1)).restore(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq(2L));
    }
}