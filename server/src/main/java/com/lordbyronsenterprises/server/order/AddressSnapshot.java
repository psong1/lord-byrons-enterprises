package com.lordbyronsenterprises.server.order;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AddressSnapshot {
    private String firstName;
    private String lastName;
    private String line1;
    private String line2;
    private String city;
    private String postalCode;
    private String country;
    private String phone;
}
