package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.service.AddressService;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AddressRestController.class)
public class AddressRestControllerTest {
    @Autowired
    private AddressRestController arc;

    @MockBean
    private AddressService addressService;

    private Pageable pageable = Pageable.unpaged();

    @Before
    public void init() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetAddressesSuccess() {
        arc.getAddresses(pageable);
        Mockito.verify(addressService, Mockito.times(1))
                .getAddressesDto(ArgumentMatchers.eq("username"), ArgumentMatchers.eq(pageable));
    }

    @Test
    public void testAddAddressSuccess() {
        arc.addAddress("name", "address");
        Mockito.verify(addressService, Mockito.times(1))
                .addAddress(ArgumentMatchers.eq("username"), ArgumentMatchers.eq("name"), ArgumentMatchers.eq("address"));
    }

    @Test
    public void testDeleteContactSuccess() {
        arc.deleteContact(2L);
        Mockito.verify(addressService, Mockito.times(1))
                .deleteAddress(ArgumentMatchers.eq("username"), ArgumentMatchers.eq(2L));
    }

    @Test
    public void testEditContactSuccess() {
        arc.editContact(2L, "name", "address");
        Mockito.verify(addressService, Mockito.times(1)).editAddress(ArgumentMatchers.eq("username"),
                ArgumentMatchers.eq(2L), ArgumentMatchers.eq("name"), ArgumentMatchers.eq("address"));
    }
}