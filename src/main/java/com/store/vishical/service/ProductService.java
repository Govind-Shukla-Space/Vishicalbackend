package com.store.vishical.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.store.vishical.dto.ProductRequest;
import com.store.vishical.entity.*;
import com.store.vishical.repository.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final ShopRepository shopRepository;
    @Autowired
    private final CloudinaryService cloudinaryService;

    public Product addProduct(Long shopId, ProductRequest req, MultipartFile file) throws IOException {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // String imageUrl = imageStorageService.storeImage(file);
        Map<String, String> uploadData = cloudinaryService.uploadImage(file);

        String imageUrl = uploadData.get("url");
        String publicId = uploadData.get("publicId");

        Product product = new Product();
        product.setName(req.getName());
        product.setPrice(req.getPrice());
        product.setDescription(req.getDescription());
        product.setImagePublicId(publicId);
        product.setImageUrl(imageUrl);
        product.setShop(shop);
        product.setShopName(shop.getShopName());
        // System.out.println(product);

        return productRepository.save(product);
    }

    public List<Product> getProductsByShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        return productRepository.findByShop(shop);
    }

    public void deleteProduct(Long productId, String loggedInEmail, Authentication auth) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getImagePublicId() != null) {
            cloudinaryService.deleteImage(product.getImagePublicId());
        }
        // from here
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // System.out.println("Is Admin: " + isAdmin+" "
        // +auth.getAuthorities().stream().map(a -> a.getAuthority()).toList());
        if (isAdmin) {
            // ADMIN can delete ANY product
            productRepository.delete(product);
            return;
        }
        // till here removw okay, only shop owner can delete their product
        // OWNER CHECK
        if (!product.getShop().getEmail().equals(loggedInEmail)) {
            throw new RuntimeException("You cannot delete another shop's product");
        }

        productRepository.delete(product);
    }

}
