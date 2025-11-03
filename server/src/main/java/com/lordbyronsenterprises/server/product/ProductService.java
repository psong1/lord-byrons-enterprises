package com.lordbyronsenterprises.server.product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDto> getAllProducts();
    Optional<ProductDto> getProductById(Long id);
    ProductDto createProduct(ProductDto product);
    ProductDto updateProduct(Long id, ProductDto product);
    void deleteProduct(Long id);
}
