package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.Address;
import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.dto.AddressDto;
import com.vladimir.mailserver.repository.AddressRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl implements AddressService {
    private AddressRepository addressRepository;
    private UserService userService;

    public AddressServiceImpl(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AddressDto> getAddressesDto(String login, Pageable pageable) {
        MailUser user = userService.find(login);
        Page<Address> addresses = addressRepository.findAllByUser(user, pageable);
        return addresses.map(address -> new AddressDto.Builder()
                .id(address.getId())
                .name(address.getName())
                .address(address.getAddress())
                .build());
    }

    @Override
    @Transactional
    public boolean addAddress(String login, String name, String address) {
        MailUser user = userService.find(login);
        Address newAddress = new Address(name, address, user);
        addressRepository.save(newAddress);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAddress(String login, Long id) {
        MailUser user = userService.find(login);
        Address address = addressRepository.findAddressById(id);
        if (address.getUser().equals(user)) {
            addressRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean editAddress(String login, Long id, String name, String newAddress) {
        MailUser user = userService.find(login);
        Address address = addressRepository.findAddressById(id);
        if (address.getUser().equals(user)) {
            address.setName(name);
            address.setAddress(newAddress);
            addressRepository.save(address);
            return true;
        }
        return false;
    }
}
