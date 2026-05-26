package com.lordbyronsenterprises.server.product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDto> getAllProducts(Pageable pageable);

    Optional<ProductDto> getProductById(Long id);
    ProductDto createProduct(ProductDto product);
    ProductDto updateProduct(Long id, ProductDto product);
    void deleteProduct(Long id);
}
