package com.store.vishical.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.vishical.dto.PasswordUpdateRequest;
import com.store.vishical.entity.Shop;
import com.store.vishical.service.ShopService;

import lombok.RequiredArgsConstructor;

@RestController
// @CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("/api/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @PostMapping("/register")
    public ResponseEntity<?> registerShop(@RequestBody Shop shop) {
        Shop savedShop = shopService.registerShop(shop);
        if (savedShop == null) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }
        return ResponseEntity.ok(savedShop);
    }

    @PreAuthorize("hasAnyRole('SHOP','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getShopDetails(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedShops() {
        return ResponseEntity.ok(shopService.getApprovedShops());
    }

    @PreAuthorize("hasRole('SHOP')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateShop(@PathVariable Long id, @RequestBody Shop shopDetails) {
        return ResponseEntity.ok(shopService.updateShop(id, shopDetails));
    }
    @PreAuthorize("hasRole('SHOP')")
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        return ResponseEntity.ok(shopService.updatePassword(request));
    }
}
