package com.store.jewellry.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.store.jewellry.dto.LoginRequest;
import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.Shop;
import com.store.jewellry.entity.User;
import com.store.jewellry.service.AuthService;
import com.store.jewellry.service.ImageStorageService;

import lombok.RequiredArgsConstructor;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private ImageStorageService imageStorageService;

    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(authService.registerUser(user));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        return ResponseEntity.ok(authService.updatePassword(request));
    }
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(authService.getUserByEmail(email));
    }
    @PreAuthorize("hasRole('USER')")  
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<ImageUploadResponse> uploadAdminImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        String imageUrl = imageStorageService.storeImage(file);
        authService.updateUserImage(id, imageUrl);

        return ResponseEntity.ok(new ImageUploadResponse(imageUrl));
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/image/{id}")
    public User getUserImage(@PathVariable Long id) {
        return authService.getUserImage(id);
    }
}