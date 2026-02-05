package com.store.jewellry.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.Admin;
import com.store.jewellry.entity.Shop;
import com.store.jewellry.entity.User;
import com.store.jewellry.repository.AdminRepository;
import com.store.jewellry.repository.ShopRepository;
import com.store.jewellry.repository.UserRepository;

@Service
public class AdminService {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public String registerAdmin(Admin admin) {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            return "Admin with this email already exists";
        }
        admin.setPassword(encoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return "Admin registered successfully";
    }
    public String approveShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) {
            return "SHOP_NOT_FOUND";
        }
        shop.setApproved(true);
        shopRepository.save(shop);
        return "SHOP_APPROVED_SUCCESSFULLY";
    }
    public List<Shop> getPendingShops() {
        return shopRepository.findByApproved(false);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }
    public String deleteShopByEmail(String email) {
        return shopRepository.findByEmail(email)
                .map(shop -> {
                    shopRepository.delete(shop);
                    return "Shop deleted successfully";
                })
                .orElse("Shop not found");
    }

    public String deleteUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    userRepository.delete(user);
                    return "User deleted successfully";
                })
                .orElse("User not found");
    }
    public String updatePassword(PasswordUpdateRequest request){
        // if (request.getEmail().equals("admin@gmail.com")) {
        //     if (!request.getOldPassword().equals("admin123")) {
        //         return "Incorrect old password for admin";
        //     }
        //     // NOTE: Real admin should be stored in DB but for now:
        //     return "Admin password updated successfully (hardcoded)";
        // }
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (!encoder.matches(request.getOldPassword(), admin.getPassword())) {
                return "Incorrect old password";
            }
            admin.setPassword(encoder.encode(request.getNewPassword()));
            adminRepository.save(admin);
            return "Admin password updated successfully";
        }
        return "Account not found!";
    }
    public void updateShopImage(Long shopId, String imageUrl) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        shop.setImageUrl(imageUrl);
        shopRepository.save(shop);
    }

    public Shop getShopImage(Long id) {
        return shopRepository.findById(id).orElseThrow();
    }
    public void updateAdminImage(Long adminId, String imageUrl) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setImageUrl(imageUrl);
        adminRepository.save(admin);
    }

    public Admin getAdminImage(Long id) {
        return adminRepository.findById(id).orElseThrow();
    }
}