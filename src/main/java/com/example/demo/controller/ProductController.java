package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

 @RestController @RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<Product> create( @RequestBody Product p) {
        Product saved = productRepository.save(p);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById( @PathVariable Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ResponseEntity.ok(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update( @PathVariable Long id, @RequestBody Product updated) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        p.setName(updated.getName());
        p.setDescription(updated.getDescription());
        p.setPrice(updated.getPrice());
        p.setQuantity(updated.getQuantity());
        productRepository.save(p);
        return ResponseEntity.ok(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete( @PathVariable Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(p);
        return ResponseEntity.noContent().build();
    }

    // GET with pagination, sorting and searching
    @GetMapping
    public ResponseEntity<Page<Product>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword
    ) {
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        Page<Product> result;
        if (keyword != null && !keyword.trim().isEmpty()) {
            result = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            result = productRepository.findAll(pageable);
        }
        return ResponseEntity.ok(result);
    }
}