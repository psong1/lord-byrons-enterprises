package com.lordbyronsenterprises.server.user;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddressesForUser(User user);
    AddressDto createAddress(User user, AddressDto addressDto);
    AddressDto updateAddress(User user, Long addressId, AddressDto addressDto);
    void deleteAddress(User user, Long addressId);
}
