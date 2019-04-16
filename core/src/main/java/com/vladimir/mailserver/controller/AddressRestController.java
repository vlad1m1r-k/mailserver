package com.vladimir.mailserver.controller;

import com.vladimir.mailserver.dto.AddressDto;
import com.vladimir.mailserver.service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
public class AddressRestController {
    private AddressService addressService;

    public AddressRestController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/get")
    public Page<AddressDto> getAddresses(Pageable pageable) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.getAddressesDto(login, pageable);
    }

    @GetMapping("/add/{name}/{address}")
    public boolean addAddress(@PathVariable String name, @PathVariable String address) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.addAddress(login, name, address);
    }

    @GetMapping("/delete/{id}")
    public boolean deleteContact(@PathVariable Long id) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.deleteAddress(login, id);
    }

    @GetMapping("/edit/{id}/{name}/{address}")
    public boolean editContact(@PathVariable Long id, @PathVariable String name, @PathVariable String address) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.editAddress(login, id, name, address);
    }
}
