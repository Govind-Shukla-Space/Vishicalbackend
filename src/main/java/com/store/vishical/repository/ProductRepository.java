package com.store.vishical.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.vishical.entity.Product;
import com.store.vishical.entity.Shop;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByShop(Shop shop);
}
