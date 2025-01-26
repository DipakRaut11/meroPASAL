package com.example.meroPASAL.Repository.cart;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.meroPASAL.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteAllByCartId(Long id);
}
