package com.example.meroPASAL.Repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.meroPASAL.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);
}
