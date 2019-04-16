package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserDetailsServiceImpl.class)
public class UserDetailsServiceImplTest {
    @Autowired
    private UserDetailsServiceImpl uds;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testLoadUserByUsernameSuccess() {
        MailUser user = new MailUser("name", "surname", "login", "password", UserRoles.USER, UserStatus.ACTIVE);
        Mockito.when(userRepository.findMailUserByLogin(Mockito.anyString())).thenReturn(user);
        UserDetails userDetails = uds.loadUserByUsername("login");
        Mockito.verify(userRepository).findMailUserByLogin(ArgumentMatchers.eq("login"));
        Assert.assertEquals("login", userDetails.getUsername());
        Assert.assertEquals("password", userDetails.getPassword());
        Assert.assertTrue(userDetails.isAccountNonExpired());
        Assert.assertTrue(userDetails.isAccountNonLocked());
        Assert.assertTrue(userDetails.isEnabled());
        Assert.assertTrue(userDetails.isCredentialsNonExpired());
        Assert.assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(UserRoles.USER.toString())));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameException() {
        uds.loadUserByUsername("login");
    }
}