package com.example.meroPASAL.service.cart;



import com.example.meroPASAL.model.Cart;
import com.example.meroPASAL.security.userModel.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCartById(Long id);
    void clearCart(Long id);
    BigDecimal getCartTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
