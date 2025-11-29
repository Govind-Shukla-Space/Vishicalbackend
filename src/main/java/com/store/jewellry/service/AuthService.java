package com.store.jewellry.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    public String registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "User with this email already exists";
        }
        userRepository.save(user);
        return "User registered successfully";
    }
    public String login(LoginRequest request){
        //user
        Optional<User> user= userRepository.findByEmail(request.getEmail());
        if(user.isPresent() && user.get().getPassword().equals(request.getPassword())){
            return "User login successful";
        }
        //check shop
        Optional<Shop> shop= shopRepository.findByEmail(request.getEmail());
        if(shop.isPresent() && shop.get().getPassword().equals(request.getPassword())){
            if(!shop.get().isApproved()){
                return "Shop not approved yet";
            }
            return "Shop login successful";
        }
        //check admin
        Optional<Admin> admin= adminRepository.findByEmail(request.getEmail());
        if(admin.isPresent() && admin.get().getPassword().equals(request.getPassword())){
        // if (request.getEmail().equals("admin@gmail.com") && request.getPassword().equals("admin123")) {
            return "ADMIN_LOGIN_SUCCESS";
        }

        return "INVALID_CREDENTIALS";
    }
    public String updatePassword(PasswordUpdateRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.getPassword().equals(request.getOldPassword())) {
                return "Incorrect old password";
            }
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
            return "User password updated successfully";
        }
        return "Account not found!";
    }
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
