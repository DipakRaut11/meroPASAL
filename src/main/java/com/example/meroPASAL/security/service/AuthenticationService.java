package com.example.meroPASAL.security.service;

import com.example.meroPASAL.security.dto.LoginRequest;
import com.example.meroPASAL.security.jwt.JwtUtils;
import com.example.meroPASAL.security.repository.RoleRepository;
import com.example.meroPASAL.security.repository.UserRepository;
import com.example.meroPASAL.security.response.SignUpResponse;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Role;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.security.userModel.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // Register a new Customer
    public SignUpResponse registerCustomer(@Valid Customer customer) {
        if (userRepository.existsByEmail(customer.getEmail())) {
            return new SignUpResponse("Email already exists");
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        assignDefaultRole(customer, "ROLE_CUSTOMER");
        userRepository.save(customer);

        return new SignUpResponse("Customer registered successfully");
    }

    // Register a new Shopkeeper
    public SignUpResponse registerShopkeeper(@Valid Shopkeeper shopkeeper) {
        if (userRepository.existsByEmail(shopkeeper.getEmail())) {
            return new SignUpResponse("Email already exists");
        }

        shopkeeper.setPassword(passwordEncoder.encode(shopkeeper.getPassword()));
        assignDefaultRole(shopkeeper, "ROLE_SHOPKEEPER");
        userRepository.save(shopkeeper);

        return new SignUpResponse("Shopkeeper registered successfully");
    }

    // Authenticate user and generate JWT token
    public HashMap<String, Object> authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenForUser(authentication);

        HashMap<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("email", userDetails.getUsername());
        response.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return response;
    }

    // Assign default role on signup
    private void assignDefaultRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        user.setRoles(Set.of(role));
    }

    // Get the currently authenticated User entity from DB
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName(); // username is email here
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get basic info about authenticated user (email, roles) for client display
    public HashMap<String, Object> getAuthenticatedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        HashMap<String, Object> response = new HashMap<>();
        response.put("email", userDetails.getUsername());
        response.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return response;
    }
}
