package com.lordbyronsenterprises.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Set;
import java.util.HashSet;

@Data
@Entity
public class ProductTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tag name is required")
    @Column(unique = true)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Product> products = new HashSet<>();
}
