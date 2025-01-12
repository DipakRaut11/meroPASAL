package com.example.meroPASAL.userRepo;

import com.example.meroPASAL.model.user.Customer;
import com.example.meroPASAL.model.user.Shopkeeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopkeeperRepo extends JpaRepository<Shopkeeper, Long> {
    Optional<Shopkeeper> findByEmail(String email);
}
