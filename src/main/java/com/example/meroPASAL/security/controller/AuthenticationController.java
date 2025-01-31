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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.stream.Collectors;

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

//    @GetMapping("/user")
//    public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
//        // Return the authenticated user's details (e.g., email, roles)
//        return ResponseEntity.ok(authenticationService.getAuthenticatedUser());
//    }
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
