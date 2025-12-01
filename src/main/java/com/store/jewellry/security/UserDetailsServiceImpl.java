package com.store.jewellry.security;

import com.store.jewellry.entity.User;
import com.store.jewellry.entity.Shop;
import com.store.jewellry.entity.Admin;
import com.store.jewellry.repository.UserRepository;
import com.store.jewellry.repository.ShopRepository;
import com.store.jewellry.repository.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ShopRepository shopRepo;

    @Autowired
    private AdminRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Admin admin = adminRepo.findByEmail(email).orElse(null);
        if (admin != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(admin.getEmail())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .build();
        }
        User user = userRepo.findByEmail(email).orElse(null);
        if (user != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        }
        Shop shop = shopRepo.findByEmail(email).orElse(null);
        if (shop != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(shop.getEmail())
                    .password(shop.getPassword())
                    .roles("SHOP")
                    .build();
        }
        throw new UsernameNotFoundException("Email not found: " + email);
    }
}
