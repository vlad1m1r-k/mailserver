package com.vladimir.mailserver.service;

import com.vladimir.mailserver.dto.AddressDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AddressService {
    Page<AddressDto> getAddressesDto(String login, Pageable pageable);
    boolean addAddress(String login, String name, String address);
    boolean deleteAddress(String login, Long id);
    boolean editAddress(String login, Long id, String name, String address);
}
