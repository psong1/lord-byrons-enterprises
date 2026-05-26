package com.lordbyronsenterprises.server.product;

import java.util.Optional;

import org.owasp.encoder.Encode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImplementation implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        final Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + productDto.getCategoryId()));

        Product product = productMapper.toEntity(productDto, category);

        product.setName(Encode.forHtml(product.getName()));
        product.setDescription(Encode.forHtml(product.getDescription()));

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productDto.getCategoryId()));

        existingProduct.setName(Encode.forHtml(productDto.getName()));
        existingProduct.setDescription(Encode.forHtml(productDto.getDescription()));
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setQuantity(productDto.getQuantity());
        existingProduct.setCategory(category);

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
