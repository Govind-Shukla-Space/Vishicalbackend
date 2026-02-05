package com.store.jewellry.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.jewellry.dto.LoginRequest;
import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.Admin;
import com.store.jewellry.entity.Shop;
import com.store.jewellry.entity.User;
import com.store.jewellry.repository.AdminRepository;
import com.store.jewellry.repository.ShopRepository;
import com.store.jewellry.repository.UserRepository;

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

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "User with this email already exists";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        // USER LOGIN
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent() && encoder.matches(request.getPassword(), user.get().getPassword())) {
            String token = jwtService.generateToken(user.get().getEmail(), "USER");
            response.put("message", "User login successful");
            response.put("role", "USER");
            response.put("email", user.get().getEmail());
            response.put("token", token);
            return response;
        }
        // SHOP LOGIN
        Optional<Shop> shop = shopRepository.findByEmail(request.getEmail());
        
        if (shop.isPresent() && encoder.matches(request.getPassword(), shop.get().getPassword())) {
            if (!shop.get().isApproved()) {
                response.put("message", "Shop not approved yet");
                return response;
            }
            
            String token = jwtService.generateToken(shop.get().getEmail(), "SHOP");
            
            response.put("message", "Shop login successful");
            response.put("role", "SHOP");
            response.put("email", shop.get().getEmail());
            response.put("token", token);
            return response;
        }
        
        // ADMIN LOGIN
        Optional<Admin> admin = adminRepository.findByEmail(request.getEmail());
        if (admin.isPresent() && encoder.matches(request.getPassword(), admin.get().getPassword())) {

            String token = jwtService.generateToken(admin.get().getEmail(), "ADMIN");
            
            response.put("message", "Admin login successful");
            response.put("role", "ADMIN");
            response.put("email", admin.get().getEmail());
            response.put("token", token);
            return response;
        }
        System.out.println("Shop details: "+shop);
        System.out.println("user details: "+user);
        System.out.println("admin details: "+admin);
        response.put("message", "INVALID_CREDENTIALS");
        return response;
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
    public void updateUserImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setImageUrl(imageUrl);
        userRepository.save(user);
    }

    public User getUserImage(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
