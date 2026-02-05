package com.store.jewellry.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.jewellry.dto.PasswordUpdateRequest;
import com.store.jewellry.entity.Shop;
import com.store.jewellry.repository.ShopRepository;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Shop registerShop(Shop shop) {
        if (shopRepository.findByEmail(shop.getEmail()).isPresent()) {
            return null;
        }
        shop.setApproved(false);
        shop.setRole("SHOP");
        shop.setPassword(encoder.encode(shop.getPassword()));
        return shopRepository.save(shop);
    }

    public Shop getShopById(Long id) {
        return shopRepository.findById(id).orElse(null);
    }

    public List<Shop> getApprovedShops() {
        return shopRepository.findByApproved(true);
    }

    public Shop updateShop(Long id, Shop details) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        if (!shop.getEmail().equals(details.getEmail())) {
            if (shopRepository.findByEmail(details.getEmail()).isPresent()) {
                throw new RuntimeException("Email already in use!");
            }
        }
        shop.setShopName(details.getShopName());
        shop.setEmail(details.getEmail());
        shop.setOwnerName(details.getOwnerName());
        shop.setAddress(details.getAddress());
        shop.setPhone(details.getPhone());

        return shopRepository.save(shop);
    }

    public String updatePassword(PasswordUpdateRequest request) {
        Optional<Shop> shopOpt = shopRepository.findByEmail(request.getEmail());

        if (shopOpt.isPresent()) {
            Shop shop = shopOpt.get();
            // Validate old password
            if (!encoder.matches(request.getOldPassword(), shop.getPassword())) {
                return "Incorrect old password";
            }
            // Encode new password
            shop.setPassword(encoder.encode(request.getNewPassword()));
            shopRepository.save(shop);
            return "Shop password updated successfully";
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
}
