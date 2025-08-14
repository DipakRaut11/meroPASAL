package com.example.meroPASAL.controller;

import com.example.meroPASAL.dto.order.OrderDto;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Order;

import com.example.meroPASAL.response.ApiResponse;

import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.User;
import com.example.meroPASAL.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

    private final IOrderService orderService;
    private final AuthenticationService authenticationService;

    @PostMapping("/order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> createOrder() {
        try {
            User user = authenticationService.getAuthenticatedUser();
            Order order = orderService.placeOrder(user.getId());
            OrderDto orderDto = orderService.convertToDto(order);
            return ResponseEntity.ok(new ApiResponse("Order Placed Successfully", orderDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error while placing order", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            User user = authenticationService.getAuthenticatedUser();
            OrderDto order = orderService.getOrderByIdForUser(orderId, user.getId());
            return ResponseEntity.ok(new ApiResponse("Order fetched successfully", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Sorry!!", e.getMessage()));
        }
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderDto>> getCurrentUserOrders() {
        User user = authenticationService.getAuthenticatedUser();
        return ResponseEntity.ok(orderService.getUserOrders(user.getId()));
    }
}
