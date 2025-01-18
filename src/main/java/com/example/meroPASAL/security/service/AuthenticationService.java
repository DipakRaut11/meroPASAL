package com.example.meroPASAL.security.service;

import com.example.meroPASAL.security.dto.LoginRequest;
import com.example.meroPASAL.security.jwt.JwtUtils;
import com.example.meroPASAL.security.repository.RoleRepository;
import com.example.meroPASAL.security.repository.UserRepository;
import com.example.meroPASAL.security.response.SignUpResponse;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Role;
import com.example.meroPASAL.security.userModel.ShopUserDetails;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.security.userModel.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

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
        assignDefaultRole(customer, "CUSTOMER");
        userRepository.save(customer);

        return new SignUpResponse("Customer registered successfully");
    }

    public SignUpResponse registerShopkeeper(@Valid Shopkeeper shopkeeper) {
        if (userRepository.existsByEmail(shopkeeper.getEmail())) {
            return new SignUpResponse("Email already exists");
        }

        shopkeeper.setPassword(passwordEncoder.encode(shopkeeper.getPassword()));
        assignDefaultRole(shopkeeper, "SHOPKEEPER");
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
}
