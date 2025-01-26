package com.example.meroPASAL.controller;

import com.example.meroPASAL.model.Cart;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.security.userModel.User;
import com.example.meroPASAL.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/cart")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CartController {
    private final CartService cartService;

    @GetMapping("/{id}")

    public ResponseEntity<ApiResponse> getCartById(@PathVariable Long id) {
       Cart cart = cartService.getCartById(id);
       return ResponseEntity.ok(new ApiResponse("Success",cart));
    }
    @DeleteMapping("/clear/{id}")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long id) {
        cartService.clearCart(id);
        return ResponseEntity.ok(new ApiResponse("Success",null));

    }
    @GetMapping("/totalPrice/{id}")
    public ResponseEntity<ApiResponse> getCartTotalPrice(@PathVariable Long id) {
        BigDecimal totalPrice = cartService.getCartTotalPrice(id);
        return ResponseEntity.ok(new ApiResponse("Success",totalPrice));
    }
    @PostMapping("/initialize/{userId}")
    public ResponseEntity<ApiResponse> initializeNewCart(@PathVariable User userId) {
        Cart cart = cartService.initializeNewCart(userId);
        return ResponseEntity.ok(new ApiResponse("Success",cart));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getCartByUserId(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(new ApiResponse("Success",cart));
    }


}
