package com.store.jewellry.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.store.jewellry.entity.Shop;
@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {
    Optional<Shop> findByEmail(String email);
    List<Shop> findByApproved(boolean approved);
}
