package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Address;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.UserRoles;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.dto.AddressDto;
import com.vladimir.mailserver.repository.AddressRepository;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AddressServiceImpl.class)
public class AddressServiceImplTest {
    @Autowired
    private AddressServiceImpl asi;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private UserService userService;
    private Pageable pageable = Pageable.unpaged();

    @Test
    public void testGetAddressesDtoSuccess() {
        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address("name1", "address1", null));
        addressList.add(new Address("name2", "address2", null));
        Page<Address> addresses = new PageImpl<>(addressList);
        Mockito.when(addressRepository.findAllByUser(Mockito.any(), ArgumentMatchers.eq(pageable))).thenReturn(addresses);
        Page<AddressDto> result = asi.getAddressesDto("login", pageable);
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("login"));
        Mockito.verify(addressRepository, Mockito.times(1)).findAllByUser(
                ArgumentMatchers.any(), ArgumentMatchers.eq(pageable));
        Assert.assertEquals("name1", result.getContent().get(0).getName());
        Assert.assertEquals("address1", result.getContent().get(0).getAddress());
        Assert.assertEquals("name2", result.getContent().get(1).getName());
        Assert.assertEquals("address2", result.getContent().get(1).getAddress());
    }

    @Test
    public void testAddAddressSuccess() {
        final Address[] addr = new Address[1];
        Mockito.when(addressRepository.save(Mockito.any())).then(obj -> addr[0] = (Address) obj.getArguments()[0]);
        asi.addAddress("login", "name", "address");
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("login"));
        Mockito.verify(addressRepository, Mockito.times(1)).save(ArgumentMatchers.any());
        Assert.assertEquals("name", addr[0].getName());
        Assert.assertEquals("address", addr[0].getAddress());
    }

    @Test
    public void testDeleteAddressSuccess() {
        MailUser user = new MailUser("name", "surname", "login", "pass", UserRoles.ADMIN, UserStatus.ACTIVE);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Address address = Mockito.mock(Address.class);
        Mockito.when(address.getUser()).thenReturn(new MailUser(null, null, "login", null, null, null));
        Mockito.when(addressRepository.findAddressById(Mockito.anyLong())).thenReturn(address);
        boolean result = asi.deleteAddress("login", 2L);
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("login"));
        Mockito.verify(addressRepository, Mockito.times(1)).findAddressById(ArgumentMatchers.eq(2L));
        Mockito.verify(addressRepository, Mockito.times(1)).deleteById(ArgumentMatchers.eq(2L));
        Assert.assertTrue(result);
    }

    @Test
    public void testDeleteAddressFail() {
        MailUser user = new MailUser("name", "surname", "login", "pass", UserRoles.ADMIN, UserStatus.ACTIVE);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Address address = Mockito.mock(Address.class);
        Mockito.when(address.getUser()).thenReturn(new MailUser(null, null, "login2", null, null, null));
        Mockito.when(addressRepository.findAddressById(Mockito.anyLong())).thenReturn(address);
        boolean result = asi.deleteAddress("login", 2L);
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("login"));
        Mockito.verify(addressRepository, Mockito.times(1)).findAddressById(ArgumentMatchers.eq(2L));
        Mockito.verify(addressRepository, Mockito.never()).deleteById(ArgumentMatchers.any());
        Assert.assertFalse(result);
    }

    @Test
    public void testEditAddressSuccess() {
        MailUser user = new MailUser("name", "surname", "login", "pass", UserRoles.ADMIN, UserStatus.ACTIVE);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Address address = Mockito.mock(Address.class);
        Mockito.when(address.getUser()).thenReturn(new MailUser(null, null, "login", null, null, null));
        Mockito.when(addressRepository.findAddressById(Mockito.anyLong())).thenReturn(address);
        boolean result = asi.editAddress("login", 2L, "name", "newaddress");
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("login"));
        Mockito.verify(addressRepository, Mockito.times(1)).findAddressById(ArgumentMatchers.eq(2L));
        Mockito.verify(addressRepository, Mockito.times(1)).save(ArgumentMatchers.any());
        Mockito.verify(address).setName(ArgumentMatchers.eq("name"));
        Mockito.verify(address).setAddress(ArgumentMatchers.eq("newaddress"));
        Assert.assertTrue(result);
    }

    @Test
    public void testEditAddressFail() {
        MailUser user = new MailUser("name", "surname", "login", "pass", UserRoles.ADMIN, UserStatus.ACTIVE);
        Mockito.when(userService.find(Mockito.anyString())).thenReturn(user);
        Address address = Mockito.mock(Address.class);
        Mockito.when(address.getUser()).thenReturn(new MailUser(null, null, "login2", null, null, null));
        Mockito.when(addressRepository.findAddressById(Mockito.anyLong())).thenReturn(address);
        boolean result = asi.editAddress("login", 2L, "name", "newaddress");
        Mockito.verify(userService, Mockito.times(1)).find(ArgumentMatchers.eq("login"));
        Mockito.verify(addressRepository, Mockito.times(1)).findAddressById(ArgumentMatchers.eq(2L));
        Mockito.verify(addressRepository, Mockito.never()).save(ArgumentMatchers.any());
        Assert.assertFalse(result);
    }
}