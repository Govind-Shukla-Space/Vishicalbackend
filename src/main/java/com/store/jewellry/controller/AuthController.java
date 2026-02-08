package com.store.jewellry.controller;

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
import com.store.jewellry.dto.LoginRequest;
import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.User;
import com.store.jewellry.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(authService.registerUser(user));
    }
    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    //     return ResponseEntity.ok(authService.login(request));
    // }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear both token and shopId cookies
        return ResponseEntity.ok(authService.logout(response));
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
}