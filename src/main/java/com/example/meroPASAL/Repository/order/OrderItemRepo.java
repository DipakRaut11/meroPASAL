package com.example.meroPASAL.Repository.order;

import com.example.meroPASAL.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    // Check if a product exists in any order
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.product.id = :productId")
    boolean existsByProductId(@Param("productId") Long productId);

    int countByProduct_Id(Long productId);
}