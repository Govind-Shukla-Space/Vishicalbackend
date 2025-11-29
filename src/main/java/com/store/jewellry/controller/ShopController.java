package com.store.jewellry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.Shop;
import com.store.jewellry.service.ShopService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin("*")
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getShopDetails(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedShops() {
        return ResponseEntity.ok(shopService.getApprovedShops());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateShop(@PathVariable Long id, @RequestBody Shop shopDetails) {
        return ResponseEntity.ok(shopService.updateShop(id, shopDetails));
    }
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        return ResponseEntity.ok(shopService.updatePassword(request));
    }
}
