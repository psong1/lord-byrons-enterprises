package com.lordbyronsenterprises.server.user;

import org.springframework.stereotype.Component;
import org.owasp.encoder.Encode;

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
        address.setLine1(Encode.forHtml(dto.getLine1()));

        if (dto.getLine2() != null) {
            address.setLine2(Encode.forHtml(dto.getLine2()));
        } else {
            address.setLine2(null);
        }

        address.setCity(Encode.forHtml(dto.getCity()));
        address.setCountry(Encode.forHtml(dto.getCountry()));
        address.setType(dto.getType());
    }
}
