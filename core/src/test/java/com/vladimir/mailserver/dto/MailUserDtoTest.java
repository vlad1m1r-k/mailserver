package com.vladimir.mailserver.dto;

import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import org.junit.Assert;
import org.junit.Test;

public class MailUserDtoTest {
    @Test
    public void testMailUserDto() {
        MailUserDto dto = MailUserDto.builder()
                .id(2L)
                .name("name")
                .surname("surname")
                .login("login")
                .role(UserRoles.ADMIN)
                .defaultAlias("alias")
                .status(UserStatus.DISABLED)
                .build();
        Assert.assertEquals(new Long(2), dto.getId());
        Assert.assertEquals("name", dto.getName());
        Assert.assertEquals("surname", dto.getSurname());
        Assert.assertEquals("login", dto.getLogin());
        Assert.assertEquals(UserRoles.ADMIN, dto.getRole());
        Assert.assertEquals("alias", dto.getDefaultAlias());
        Assert.assertEquals(UserStatus.DISABLED, dto.getStatus());
    }
}