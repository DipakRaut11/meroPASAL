package com.example.meroPASAL.Repository.order;

import com.example.meroPASAL.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    // Check if a product exists in any order
    boolean existsByProduct_Id(Long productId);
}