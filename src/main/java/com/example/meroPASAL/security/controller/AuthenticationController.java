package com.example.meroPASAL.security.controller;

import com.example.meroPASAL.security.dto.LoginRequest;
import com.example.meroPASAL.security.response.SignUpResponse;
import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("${api.prefix}/auth")

@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup/customer")
    public ResponseEntity<SignUpResponse> signupCustomer(@Valid @RequestBody Customer customer) {
        SignUpResponse response = authenticationService.registerCustomer(customer);
        if (response.getMessage().equals("Email already exists")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/shopkeeper")
    public ResponseEntity<SignUpResponse> signupShopkeeper(@Valid @RequestBody Shopkeeper shopkeeper) {
        SignUpResponse response = authenticationService.registerShopkeeper(shopkeeper);
        if (response.getMessage().equals("Email already exists")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }
}
