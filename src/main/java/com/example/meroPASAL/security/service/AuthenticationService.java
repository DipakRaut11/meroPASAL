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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;


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

    public SignUpResponse registerCustomer(@Valid Customer customer) {
        if (userRepository.existsByEmail(customer.getEmail())) {
            return new SignUpResponse("Email already exists");
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        assignDefaultRole(customer, "ROLE_CUSTOMER");
        userRepository.save(customer);

        return new SignUpResponse("Customer registered successfully");
    }

    public SignUpResponse registerShopkeeper(@Valid Shopkeeper shopkeeper) {
        if (userRepository.existsByEmail(shopkeeper.getEmail())) {
            return new SignUpResponse("Email already exists");
        }

        shopkeeper.setPassword(passwordEncoder.encode(shopkeeper.getPassword()));
        assignDefaultRole(shopkeeper, "ROLE_SHOPKEEPER");
        userRepository.save(shopkeeper);

        return new SignUpResponse("Shopkeeper registered successfully");
    }

    public HashMap<String, Object> authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenForUser(authentication);

        HashMap<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("email", userDetails.getEmail());
        response.put("roles", userDetails.getAuthorities());

        return response;
    }

    private void assignDefaultRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        user.setRoles(Set.of(role));
    }


    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public HashMap<String, Object> getAuthenticatedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        HashMap<String, Object> response = new HashMap<>();
        response.put("email", userDetails.getUsername());
        response.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return response;
    }
}
