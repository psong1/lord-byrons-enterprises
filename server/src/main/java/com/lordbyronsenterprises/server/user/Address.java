package com.lordbyronsenterprises.server.user;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Data
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Street address is required")
    private String line1;

    private String line2;

    @NotBlank(message = "City is required")
    private String city = "Eleuthera";

    @NotBlank(message = "Country is required")
    private String country = "Bahamas";

    @Enumerated(EnumType.STRING)
    private AddressType type;

    private enum AddressType {
        BILLING,
        SHIPPING
    }
}
