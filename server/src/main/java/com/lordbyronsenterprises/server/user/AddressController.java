package com.lordbyronsenterprises.server.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getMyAddresses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(addressService.getAddressesForUser(user));
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddressDto addressDto
    ) {
        AddressDto newAddress = addressService.createAddress(user, addressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDto addressDto
    ) {
        AddressDto updatedAddress = addressService.updateAddress(user, addressId, addressDto);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId
    ) {
        addressService.deleteAddress(user, addressId);
        return ResponseEntity.noContent().build();
    }
}
