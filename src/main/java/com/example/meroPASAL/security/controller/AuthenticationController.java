package com.example.meroPASAL.security.controller;

import com.example.meroPASAL.security.dto.LoginRequest;
import com.example.meroPASAL.security.response.SignUpResponse;
import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;


import java.util.HashMap;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup/customer")
    public ResponseEntity<SignUpResponse> signupCustomer(@Valid @RequestBody Customer customer) {
        SignUpResponse response = authenticationService.registerCustomer(customer);
        if ("Email already exists".equals(response.getMessage())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/shopkeeper")
    public ResponseEntity<SignUpResponse> signupShopkeeper(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String shopName,
            @RequestParam String address,
            @RequestParam String panNumber,
            @RequestParam String contactNumber,
            @RequestParam(required = false) MultipartFile businessQR
    ) {
        try {
            Shopkeeper shopkeeper = new Shopkeeper();
            shopkeeper.setName(name);
            shopkeeper.setEmail(email);
            shopkeeper.setPassword(password);
            shopkeeper.setShopName(shopName);
            shopkeeper.setAddress(address);
            shopkeeper.setPanNumber(panNumber);
            shopkeeper.setContactNumber(contactNumber);

            if (businessQR != null && !businessQR.isEmpty()) {
                shopkeeper.setBusinessQR(businessQR.getBytes());
            }

            SignUpResponse response = authenticationService.registerShopkeeper(shopkeeper);

            if ("Email already exists".equals(response.getMessage()) ||
                    "PAN number already exists".equals(response.getMessage())) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SignUpResponse("Error: " + e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAuthenticatedUser() {
        try {
            HashMap<String, Object> response = authenticationService.getAuthenticatedUserDetails();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
