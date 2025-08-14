package com.example.meroPASAL.controller;

import com.example.meroPASAL.model.Cart;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.security.service.AuthenticationService;
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
@PreAuthorize("hasAnyRole('CUSTOMER', 'SHOPKEEPER')")

public class CartController {

    private final CartService cartService;
    private final AuthenticationService authenticationService;


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCartById(@PathVariable Long id) {
        Cart cart = cartService.getCartById(id);
        return ResponseEntity.ok(new ApiResponse("Success", cart));
    }

    @DeleteMapping("/clear/{id}")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long id) {
        cartService.clearCart(id);
        return ResponseEntity.ok(new ApiResponse("Cart cleared successfully", null));
    }

    @GetMapping("/totalPrice/{id}")
    public ResponseEntity<ApiResponse> getCartTotalPrice(@PathVariable Long id) {
        BigDecimal totalPrice = cartService.getCartTotalPrice(id);
        return ResponseEntity.ok(new ApiResponse("Success", totalPrice));
    }

    // Updated: always create a new cart linked to the authenticated user
    @PostMapping("/initialize")
    public ResponseEntity<ApiResponse> initializeNewCart(@RequestBody User user) {
        Cart cart = cartService.initializeNewCart(user);
        return ResponseEntity.ok(new ApiResponse("New cart initialized successfully", cart));
    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<ApiResponse> getCartByUserId(@PathVariable Long userId) {
//        Cart cart = cartService.getCartByUserId(userId);
//        return ResponseEntity.ok(new ApiResponse("Success", cart));
//    }

    //  New endpoint: get logged-in user's cart
    @GetMapping("/my-cart")
    public ResponseEntity<ApiResponse> getMyCart() {
        User user = authenticationService.getAuthenticatedUser();
        Cart cart = cartService.getCartByUserId(user.getId());
        return ResponseEntity.ok(new ApiResponse("Success", cart));
    }
}
