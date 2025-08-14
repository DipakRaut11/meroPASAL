package com.example.meroPASAL.service.cart;

import com.example.meroPASAL.Repository.cart.CartItemRepository;
import com.example.meroPASAL.Repository.cart.CartRepository;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Cart;
import com.example.meroPASAL.security.userModel.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + id));
        // Optionally, calculate totalAmount here if needed
        return cart;
    }

    @Transactional
    @Override
    public void clearCart(Long id) {
        Cart cart = getCartById(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getItems().clear();
        cartRepository.deleteById(id);
    }

    @Override
    public BigDecimal getCartTotalPrice(Long id) {
        Cart cart = getCartById(id);
        return cart.getTotalAmount();
    }

    // Updated: always create new cart linked to user
    @Override
    public Cart initializeNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user); // Link cart to user
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}
