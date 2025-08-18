package com.example.meroPASAL.security.repository;

import com.example.meroPASAL.security.userModel.Shopkeeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopkeeperRepositoryQr extends JpaRepository<Shopkeeper, Long> {

    Optional<Shopkeeper> findById(Long id);

}
