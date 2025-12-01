package com.store.jewellry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.Admin;
import com.store.jewellry.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.registerAdmin(admin));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveShop(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveShop(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingShops() {
        return ResponseEntity.ok(adminService.getPendingShops());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/shops")
    public ResponseEntity<?> getAllShops() {
      return ResponseEntity.ok(adminService.getAllShops());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return ResponseEntity.ok(adminService.deleteUserByEmail(email));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/shop/{email}")
    public ResponseEntity<?> deleteShop(@PathVariable String email) {
        return ResponseEntity.ok(adminService.deleteShopByEmail(email));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        return ResponseEntity.ok(adminService.updatePassword(request));
    }
}
