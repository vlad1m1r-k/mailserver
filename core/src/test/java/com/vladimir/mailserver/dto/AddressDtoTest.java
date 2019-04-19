package com.vladimir.mailserver.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddressDtoTest {
    @Test
    public void testAddressDto() {
        AddressDto dto = AddressDto.builder()
                .id(2L)
                .name("name")
                .address("address")
                .build();
        assertEquals(new Long(2), dto.getId());
        assertEquals("name", dto.getName());
        assertEquals("address", dto.getAddress());
    }
}