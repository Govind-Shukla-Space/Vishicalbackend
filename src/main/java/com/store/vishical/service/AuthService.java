package com.store.vishical.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.vishical.dto.LoginRequest;
import com.store.vishical.dto.LogoutResponse;
import com.store.vishical.dto.PasswordUpdateRequest;
import com.store.vishical.entity.Admin;
import com.store.vishical.entity.Shop;
import com.store.vishical.entity.User;
import com.store.vishical.repository.AdminRepository;
import com.store.vishical.repository.ShopRepository;
import com.store.vishical.repository.UserRepository;
import com.store.vishical.security.CookieUtil;
import com.store.vishical.security.JwtService;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CookieUtil cookieUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "User with this email already exists";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    public Map<String, Object> login(LoginRequest request, HttpServletResponse response) {

        Map<String, Object> responsebODY = new HashMap<>();
        // USER LOGIN
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent() && encoder.matches(request.getPassword(), user.get().getPassword())) {
            String token = jwtService.generateToken(user.get().getEmail(), "USER");
            cookieUtil.addAuthCookies(response, token, null);
            responsebODY.put("id", user.get().getId());
            responsebODY.put("message", "User login successful");
            responsebODY.put("role", "USER");
            responsebODY.put("email", user.get().getEmail());
            // responsebODY.put("token", token);
            return responsebODY;
        }
        // SHOP LOGIN
        Optional<Shop> shop = shopRepository.findByEmail(request.getEmail());
        
        if (shop.isPresent() && encoder.matches(request.getPassword(), shop.get().getPassword())) {
            if (!shop.get().isApproved()) {
                responsebODY.put("message", "Shop not approved yet");
                return responsebODY;
            }
            
            String token = jwtService.generateToken(shop.get().getEmail(), "SHOP");
            String shopId=shop.get().getId().toString();
            cookieUtil.addAuthCookies(response, token, shopId);
            responsebODY.put("id", shop.get().getId());
            responsebODY.put("message", "Shop login successful");
            responsebODY.put("role", "SHOP");
            responsebODY.put("email", shop.get().getEmail());
            responsebODY.put("shopId", shopId);
            // responsebODY.put("token", token);
            return responsebODY;
        }
        
        // ADMIN LOGIN
        Optional<Admin> admin = adminRepository.findByEmail(request.getEmail());
        if (admin.isPresent() && encoder.matches(request.getPassword(), admin.get().getPassword())) {

            String token = jwtService.generateToken(admin.get().getEmail(), "ADMIN");
            cookieUtil.addAuthCookies(response, token, null);
            responsebODY.put("id", admin.get().getId());
            responsebODY.put("message", "Admin login successful");
            responsebODY.put("role", "ADMIN");
            responsebODY.put("email", admin.get().getEmail());
            // responsebODY.put("token", token);
            return responsebODY;
        }
        responsebODY.put("message", "INVALID_CREDENTIALS");
        return responsebODY;
    }
    public LogoutResponse logout(HttpServletResponse response) {
        cookieUtil.clearAuthCookies(response);
        return new LogoutResponse("Logged out successfully");
    }
    public String updatePassword(PasswordUpdateRequest request) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
                return "Incorrect old password";
            }
            user.setPassword(encoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return "User password updated successfully";
        }
        return "Account not found!";
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
