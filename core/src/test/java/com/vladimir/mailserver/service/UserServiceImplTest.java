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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceImpl.class)
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl usi;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrgSettingsService orgSettingsService;

    @MockBean
    private MailboxRepository mailboxRepository;

    @MockBean
    private MailAliasRepository aliasRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;
    private Pageable pageable = Pageable.unpaged();

    @Test
    public void testCreateUserSuccess() {
        final MailUser[] users = new MailUser[1];
        Mockito.when(userRepository.save(Mockito.any())).then(o -> users[0] = o.getArgument(0));
        final Mailbox[] mailboxes = new Mailbox[1];
        Mockito.when(mailboxRepository.save(Mockito.any())).then(o -> mailboxes[0] = o.getArgument(0));
        final MailboxAlias[] aliases = new MailboxAlias[1];
        Mockito.when(aliasRepository.save(Mockito.any())).then(o -> aliases[0] = o.getArgument(0));
        Mockito.when(orgSettingsService.getDefaultDomain()).thenReturn(new AcceptedDomain("domain", null));
        usi.createUser("name", "surname", "login", "password");
        Mockito.verify(passwordEncoder).encode(ArgumentMatchers.eq("password"));
        Mockito.verify(userRepository).save(ArgumentMatchers.any());
        Assert.assertEquals("name", users[0].getName());
        Assert.assertEquals("surname", users[0].getSurname());
        Assert.assertEquals("login", users[0].getLogin());
        Assert.assertEquals(UserRoles.USER, users[0].getRole());
        Assert.assertEquals(UserStatus.ACTIVE, users[0].getStatus());
        Mockito.verify(mailboxRepository).save(ArgumentMatchers.any());
        Assert.assertEquals(users[0], mailboxes[0].getUser());
        Mockito.verify(orgSettingsService).getDefaultDomain();
        Mockito.verify(aliasRepository).save(ArgumentMatchers.any());
        Assert.assertEquals("login@domain", aliases[0].getValue());
        Assert.assertEquals(mailboxes[0], aliases[0].getMailbox());
        Assert.assertTrue(aliases[0].isDefault());
    }

    @Test
    public void testFindSuccess() {
        usi.find("login");
        Mockito.verify(userRepository).findMailUserByLogin(ArgumentMatchers.eq("login"));
    }

    @Test
    public void testGetUserDtoSuccess() throws NoSuchFieldException, IllegalAccessException {
        MailUser user = new MailUser("name", "surname", "login", null, UserRoles.USER, null);
        Field mailboxField = MailUser.class.getDeclaredField("mailbox");
        mailboxField.setAccessible(true);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        mailboxField.set(user, mailbox);
        MailboxAlias alias = new MailboxAlias("login@domain", null, true, null);
        Mockito.when(mailbox.getDefaultAlias()).thenReturn(alias);
        Mockito.when(userRepository.findMailUserByLogin(Mockito.anyString())).thenReturn(user);
        MailUserDto userDto = usi.getUserDto("login");
        Assert.assertEquals("name", userDto.getName());
        Assert.assertEquals("surname", userDto.getSurname());
        Assert.assertEquals("login", userDto.getLogin());
        Assert.assertEquals(UserRoles.USER, userDto.getRole());
        Assert.assertEquals("login@domain", userDto.getDefaultAlias());
    }

    @Test
    public void testUpdateUserSuccess() {
        MailUser user = new MailUser("name", "surname", "login", "userpassword", UserRoles.USER, null);
        Mockito.when(userRepository.findMailUserByLogin(Mockito.anyString())).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPass");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        boolean result = usi.updateUser("login", "newname", "newsurname", "password", "newpassword");
        Mockito.verify(passwordEncoder).matches(ArgumentMatchers.eq("password"), ArgumentMatchers.eq("userpassword"));
        Assert.assertEquals("newname", user.getName());
        Assert.assertEquals("newsurname", user.getSurname());
        Assert.assertEquals("encodedPass", user.getPassword());
        Mockito.verify(passwordEncoder).encode(ArgumentMatchers.eq("newpassword"));
        Mockito.verify(userRepository).save(ArgumentMatchers.eq(user));
        Assert.assertTrue(result);
    }

    @Test
    public void testUpdateUserWithoutPassword() {
        MailUser user = new MailUser("name", "surname", "login", "userpassword", UserRoles.USER, null);
        Mockito.when(userRepository.findMailUserByLogin(Mockito.anyString())).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPass");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        boolean result = usi.updateUser("login", "newname", "newsurname", "password", null);
        Mockito.verify(passwordEncoder).matches(ArgumentMatchers.eq("password"), ArgumentMatchers.eq("userpassword"));
        Assert.assertEquals("newname", user.getName());
        Assert.assertEquals("newsurname", user.getSurname());
        Assert.assertEquals("userpassword", user.getPassword());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.any());
        Mockito.verify(userRepository).save(ArgumentMatchers.eq(user));
        Assert.assertTrue(result);
    }

    @Test
    public void testUpdateUserFail() {
        MailUser user = new MailUser("name", "surname", "login", "userpassword", UserRoles.USER, null);
        Mockito.when(userRepository.findMailUserByLogin(Mockito.anyString())).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPass");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        boolean result = usi.updateUser("login", "newname", "newsurname", "password", "newPassword");
        Mockito.verify(passwordEncoder).matches(ArgumentMatchers.eq("password"), ArgumentMatchers.eq("userpassword"));
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.any());
        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any());
        Assert.assertFalse(result);
    }

    @Test
    public void testGetUsersSuccess() {
        List<MailUser> userList = new ArrayList<>();
        userList.add(new MailUser());
        userList.add(new MailUser("name", "surname", "login", null, UserRoles.USER, UserStatus.ACTIVE));
        userList.add(new MailUser());
        Page<MailUser> userPage = new PageImpl<>(userList);
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(userPage);
        Page<MailUserDto> userDtos = usi.getUsers(pageable);
        Assert.assertEquals("name", userDtos.getContent().get(1).getName());
        Assert.assertEquals("surname", userDtos.getContent().get(1).getSurname());
        Assert.assertEquals("login", userDtos.getContent().get(1).getLogin());
        Assert.assertEquals(UserRoles.USER, userDtos.getContent().get(1).getRole());
        Assert.assertEquals(UserStatus.ACTIVE, userDtos.getContent().get(1).getStatus());
    }

    @Test
    public void testEditUserSuccess() {
        MailUser user = new MailUser("name", "surname", "login", "password", UserRoles.USER, UserStatus.ACTIVE);
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPass");
        usi.editUser(2L, "newname", "newsurname", UserRoles.ADMIN, UserStatus.DISABLED, "newpassword");
        Mockito.verify(userRepository).getOne(ArgumentMatchers.eq(2L));
        Mockito.verify(passwordEncoder).encode(ArgumentMatchers.eq("newpassword"));
        Mockito.verify(userRepository).save(ArgumentMatchers.eq(user));
        Assert.assertEquals("newname", user.getName());
        Assert.assertEquals("newsurname", user.getSurname());
        Assert.assertEquals("encodedPass", user.getPassword());
        Assert.assertEquals(UserRoles.ADMIN, user.getRole());
        Assert.assertEquals(UserStatus.DISABLED, user.getStatus());
    }

    @Test
    public void testEditUserNotFound() {
        usi.editUser(2L, "newname", "newsurname", UserRoles.ADMIN, UserStatus.DISABLED, "newpassword");
        Mockito.verify(userRepository).getOne(ArgumentMatchers.eq(2L));
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.any());
        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void testGetAliasesSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(user);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        List<MailboxAlias> aliasList = new ArrayList<>();
        aliasList.add(new MailboxAlias("alias1", null, false, null));
        aliasList.add(new MailboxAlias("alias2", null, true, null));
        Mockito.when(mailbox.getAliases()).thenReturn(aliasList);
        List<MailboxAliasDto> aliasDtos = usi.getAliases(2L);
        Mockito.verify(userRepository).getOne(ArgumentMatchers.eq(2L));
        Assert.assertEquals("alias2", aliasDtos.get(1).getName());
        Assert.assertTrue(aliasDtos.get(1).getDefault());
    }

    @Test
    public void testAddAliasSuccess() {
        MailUser user = Mockito.mock(MailUser.class);
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(user);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Mockito.when(user.getMailbox()).thenReturn(mailbox);
        AcceptedDomain domain = new AcceptedDomain();
        Mockito.when(orgSettingsService.getDomainByName(Mockito.anyString())).thenReturn(domain);
        final MailboxAlias[] alias = new MailboxAlias[1];
        Mockito.when(aliasRepository.save(Mockito.any())).then(o -> alias[0] = o.getArgument(0));
        usi.addAlias(2L, "aliAs@domain");
        Mockito.verify(userRepository).getOne(ArgumentMatchers.eq(2L));
        Mockito.verify(orgSettingsService).getDomainByName(ArgumentMatchers.eq("domain"));
        Mockito.verify(aliasRepository).save(ArgumentMatchers.any());
        Assert.assertEquals("alias@domain", alias[0].getValue());
        Assert.assertEquals(mailbox, alias[0].getMailbox());
        Assert.assertFalse(alias[0].isDefault());
    }

    @Test
    public void testDeleteAliasSuccess() {
        Mockito.when(aliasRepository.getOne(Mockito.anyLong())).thenReturn(new MailboxAlias(null, null, false, null));
        boolean result = usi.deleteAlias(2L);
        Mockito.verify(aliasRepository).getOne(ArgumentMatchers.eq(2L));
        Mockito.verify(aliasRepository).delete(ArgumentMatchers.any(MailboxAlias.class));
        Assert.assertTrue(result);
    }

    @Test
    public void testDeleteAliasFail() {
        Mockito.when(aliasRepository.getOne(Mockito.anyLong())).thenReturn(new MailboxAlias(null, null, true, null));
        boolean result = usi.deleteAlias(2L);
        Mockito.verify(aliasRepository).getOne(ArgumentMatchers.eq(2L));
        Mockito.verify(aliasRepository, Mockito.never()).delete(ArgumentMatchers.any());
        Assert.assertFalse(result);
    }

    @Test
    public void testEditAliasNoDefaultSuccess() {
        MailboxAlias alias = new MailboxAlias(null, null, false, null);
        Mockito.when(aliasRepository.getOne(Mockito.anyLong())).thenReturn(alias);
        usi.editAlias(2L, "newAliasname@domain", false);
        Mockito.verify(aliasRepository).getOne(ArgumentMatchers.eq(2L));
        Mockito.verify(orgSettingsService).getDomainByName(ArgumentMatchers.eq("domain"));
        Assert.assertEquals("newaliasname@domain", alias.getValue());
        Assert.assertFalse(alias.isDefault());
        Mockito.verify(aliasRepository).save(ArgumentMatchers.eq(alias));
    }

    @Test
    public void testEditAliasDoDefaultSuccess() throws NoSuchFieldException, IllegalAccessException {
        MailboxAlias alias = new MailboxAlias();
        Mockito.when(aliasRepository.getOne(Mockito.anyLong())).thenReturn(alias);
        Mailbox mailbox = Mockito.mock(Mailbox.class);
        Field mailboxField = MailboxAlias.class.getDeclaredField("mailbox");
        mailboxField.setAccessible(true);
        mailboxField.set(alias, mailbox);
        MailboxAlias defaultAlias = new MailboxAlias(null, null, true, null);
        Mockito.when(mailbox.getDefaultAlias()).thenReturn(defaultAlias);
        usi.editAlias(2L, "newAliasname@domain", true);
        Mockito.verify(aliasRepository, Mockito.times(2)).getOne(ArgumentMatchers.eq(2L));
        Mockito.verify(aliasRepository).save(ArgumentMatchers.eq(defaultAlias));
        Assert.assertFalse(defaultAlias.isDefault());
        Mockito.verify(aliasRepository).save(ArgumentMatchers.eq(alias));
        Assert.assertTrue(alias.isDefault());
    }
}