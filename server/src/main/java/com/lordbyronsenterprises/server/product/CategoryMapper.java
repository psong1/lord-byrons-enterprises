package com.lordbyronsenterprises.server.product;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
public class CategoryMapper {

    public Product.CategoryDto toDto(Category category) {
        Product.CategoryDto dto = new Product.CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    public Category toEntity(Product.CategoryDto dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    @RestController
    @RequestMapping("/product")
    public static class ProductController {

        private final ProductService productService;
        private final ProductMapper productMapper;
        private final CategoryService categoryService;

        public ProductController(ProductService productService, ProductMapper productMapper, CategoryService categoryService) {
            this.productService = productService;
            this.productMapper = productMapper;
            this.categoryService = categoryService;
        }

        @GetMapping
        public List<Product.ProductDto> getAll() {
            return productService.getAllProducts()
                    .stream()
                    .map(productMapper::toDto)
                    .toList();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Product.ProductDto> getProductById(@PathVariable Long id) {
            return productService.getProductById(id)
                    .map(productMapper::toDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<Product.ProductDto> create(@RequestBody Product.ProductDto productDto) {
            try {
                Category category = categoryService.getCategoryById(productDto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDto.getCategoryId()));
                Product product = productMapper.toEntity(productDto, category);
                Product savedProduct = productService.createProduct(product);
                return ResponseEntity.ok(productMapper.toDto(savedProduct));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().build();
            }

        }

        @PutMapping("/{id}")
        public ResponseEntity<Product.ProductDto> update(@PathVariable Long id, @RequestBody Product.ProductDto productDto) {
            try {
                Category category = categoryService.getCategoryById(productDto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDto.getCategoryId()));
                Product product = productService.updateProduct(id, productMapper.toEntity(productDto, category));
                return ResponseEntity.ok(productMapper.toDto(product));
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
            try {
                productService.deleteProduct(id);
                return ResponseEntity.ok().build();
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }
    }
}
