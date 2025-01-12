package com.example.meroPASAL.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;  // For Customer
    private String shopName; // For Shopkeeper
    private String email;
    private String password;
    private String confirmPassword;  // You may want to double-check the spelling (confirmPassword might be the expected name)
    private String PAN; // For Shopkeeper
}
