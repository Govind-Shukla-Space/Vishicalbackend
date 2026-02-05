package com.store.jewellry.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.store.jewellry.dto.ImageUploadResponse;
import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.Admin;
import com.store.jewellry.entity.Shop;
import com.store.jewellry.service.AdminService;
import com.store.jewellry.service.ImageStorageService;

import org.springframework.web.bind.annotation.CrossOrigin;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private ImageStorageService imageStorageService;
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
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        return ResponseEntity.ok(adminService.updatePassword(request));
    }

    @GetMapping("/debug")
    public String debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().toString();
    }

    @PreAuthorize("hasRole('ADMIN')")  
    @PutMapping("/{id}/upload-image")
    public ResponseEntity<ImageUploadResponse> uploadShopImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        String imageUrl = imageStorageService.storeImage(file);
        adminService.updateAdminImage(id, imageUrl);

        return ResponseEntity.ok(new ImageUploadResponse(imageUrl));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/image/{id}")
    public Admin getAdminImage(@PathVariable Long id) {
        return adminService.getAdminImage(id);
    }
}
