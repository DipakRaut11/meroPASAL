package com.example.meroPASAL.controller;

import com.example.meroPASAL.dto.order.OrderDto;
import com.example.meroPASAL.dto.order.OrderRequestDto;
import com.example.meroPASAL.dto.order.OrderSummaryDto;
import com.example.meroPASAL.enums.OderStatus;
import com.example.meroPASAL.enums.PaymentStatus;
import com.example.meroPASAL.model.Order;
import com.example.meroPASAL.model.Payment;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.security.userModel.User;
import com.example.meroPASAL.service.order.IOrderService;
import com.example.meroPASAL.service.order.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

    private final IOrderService orderService;
    private final AuthenticationService authenticationService;
    private final PaymentService paymentService;

    @Value("${esewa.sandbox.url}")
    private String esewaSandboxUrl;

    // ------------------- CUSTOMER -------------------

//    @PostMapping("/order")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public ResponseEntity<ApiResponse> createOrder(@RequestParam(defaultValue = "100") double amount) throws Exception{
//
//    Payment payment = paymentService.preparePayment(amount);
//        return ResponseEntity.ok(new ApiResponse("eSewa payment details generated", payment));
//
//
//    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> createOrder(@RequestBody OrderRequestDto orderRequest) throws Exception {
        Payment payment = paymentService.preparePayment(
                orderRequest.getAmount(),
                orderRequest.getDropLocation(),
                orderRequest.getLandmark(),
                orderRequest.getReceiverContact()
        );
        return ResponseEntity.ok(new ApiResponse("eSewa payment details generated", payment));
    }



    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            User user = authenticationService.getAuthenticatedUser();
            OrderDto order = orderService.getOrderByIdForUser(orderId, user.getId());
            return ResponseEntity.ok(new ApiResponse("Order fetched successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Error fetching order", e.getMessage()));
        }
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderDto>> getCurrentUserOrders() {
        User user = authenticationService.getAuthenticatedUser();
        return ResponseEntity.ok(orderService.getUserOrders(user.getId()));
    }

    // ------------------- SHOPKEEPER -------------------

    @GetMapping("/shop-orders")
    @PreAuthorize("hasRole('SHOPKEEPER')")
    public ResponseEntity<List<OrderDto>> getOrdersForShop() {
        Shopkeeper shopkeeperUser = (Shopkeeper) authenticationService.getAuthenticatedUser();
        Long shopkeeperId = shopkeeperUser.getId();
        List<OrderDto> orders = orderService.getOrdersByShop(shopkeeperId);
        return ResponseEntity.ok(orders);
    }
//
//    @PutMapping("/{orderId}/status")
//    @PreAuthorize("hasRole('SHOPKEEPER')")
//    public ResponseEntity<ApiResponse> updateOrderStatus(
//            @PathVariable Long orderId,
//            @RequestParam OderStatus status
//    ) {
//        try {
//            Shopkeeper shopkeeperUser = (Shopkeeper) authenticationService.getAuthenticatedUser();
//            Long shopkeeperId = shopkeeperUser.getId();
//            OrderDto updatedOrder = orderService.updateOrderStatus(orderId, status, shopkeeperId);
//            return ResponseEntity.ok(new ApiResponse("Order status updated", updatedOrder));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse("Error updating order status", e.getMessage()));
//        }
//    }

    // ------------------- ADMIN -------------------


    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")   // ✅ Only ADMIN can update order status now
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OderStatus status
    ) {
        try {
            // No longer tied to a shopkeeper
            OrderDto updatedOrder = orderService.updateOrderStatusByAdmin(orderId, status);
            return ResponseEntity.ok(new ApiResponse("Order status updated", updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Error updating order status", e.getMessage()));
        }
    }


    @PutMapping("/{orderId}/payment-status")
    @PreAuthorize("hasRole('ADMIN')") // or CUSTOMER if allowed
    public ResponseEntity<ApiResponse> updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestParam PaymentStatus paymentStatus) {
        try {
            OrderSummaryDto updatedOrder = orderService.updatePaymentStatus(orderId, paymentStatus);
            return ResponseEntity.ok(new ApiResponse("Payment status updated", updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Error updating payment status", e.getMessage()));
        }
    }


    @GetMapping("/all")
     @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderSummaryDto>> getAllOrders() {
        List<OrderSummaryDto> orders = orderService.getAllOrders();

        // Optional: sort by order date or any other criteria if needed
        orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate())); // newest first

        return ResponseEntity.ok(orders);
    }



}