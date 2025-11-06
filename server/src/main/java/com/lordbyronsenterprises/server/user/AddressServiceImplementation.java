package com.lordbyronsenterprises.server.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.owasp.encoder.Encode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImplementation implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesForUser(User user) {
        return addressRepository.findByUser(user).stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDto createAddress(User user, AddressDto addressDto) {
        Address address = new Address();

        addressMapper.toEntity(addressDto, address);

        address.setUser(user); // Links to the authenticated user
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    @Override
    public AddressDto updateAddress(User user, Long addressId, AddressDto addressDto) {
        Address address = findAndVerifyAddress(user, addressId);

        addressMapper.toEntity(addressDto, address);

        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toDto(updatedAddress);
    }

    @Override
    public void deleteAddress(User user, Long addressId) {
        Address address = findAndVerifyAddress(user, addressId);
        addressRepository.delete(address);
    }

    private Address findAndVerifyAddress(User user, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("User does not have permission to access this address");
        }

        return address;
    }
}
