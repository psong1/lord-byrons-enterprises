package com.lordbyronsenterprises.server.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    @RestController
    @RequestMapping("/category")
    public static class CategoryController {

        private final CategoryService categoryService;
        private final CategoryMapper categoryMapper;

        public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
            this.categoryService = categoryService;
            this.categoryMapper = categoryMapper;
        }

        @GetMapping
        public List<Product.CategoryDto> getAll() {
            return categoryService.getAllCategories()
                    .stream()
                    .map(categoryMapper::toDto)
                    .toList();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Product.CategoryDto> getCategoryById(@PathVariable Long id) {
            return categoryService.getCategoryById(id)
                    .map(categoryMapper::toDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<Product.CategoryDto> create(@RequestBody Product.CategoryDto dto) {
            try {
                Category category = categoryMapper.toEntity(dto);
                Category savedCategory = categoryService.createCategory(category);
                return ResponseEntity.ok(categoryMapper.toDto(savedCategory));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @PutMapping("/{id}")
        public ResponseEntity<Product.CategoryDto> update(@PathVariable Long id, @RequestBody Product.CategoryDto dto) {
            try {
                Category category = categoryService.updateCategory(id, categoryMapper.toEntity(dto));
                return ResponseEntity.ok(categoryMapper.toDto(category));
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
            try {
                categoryService.deleteCategory(id);
                return ResponseEntity.ok().build();
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }
    }
}
