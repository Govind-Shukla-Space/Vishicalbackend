package com.store.vishical.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.store.vishical.dto.ProductRequest;
import com.store.vishical.entity.Product;
import com.store.vishical.service.ProductService;

import java.io.IOException;
import java.util.List;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private final ProductService productService;

    @PreAuthorize("hasRole('SHOP')")
    @PostMapping(value = "/{shopId}/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProduct(
            @PathVariable Long shopId,
            @RequestPart("product") ProductRequest request,
            @RequestPart("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(productService.addProduct(shopId, request, file));
    }

    // Anyone can view shop products
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Product>> getProducts(@PathVariable Long shopId) {
        return ResponseEntity.ok(productService.getProductsByShop(shopId));
    }
    
    @PreAuthorize("hasAnyRole('SHOP','ADMIN')")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long productId,
            Authentication auth) throws IOException {
        String email = auth.getName(); // Logged-in shop email
        productService.deleteProduct(productId, email,auth);

        return ResponseEntity.ok("Product deleted");
    }
}
