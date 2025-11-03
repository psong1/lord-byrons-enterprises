package com.lordbyronsenterprises.server.user;

import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDto toDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        dto.setType(address.getType());
        return dto;
    }

    public void toEntity(AddressDto dto, Address address) {
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setType(dto.getType());
    }
}
