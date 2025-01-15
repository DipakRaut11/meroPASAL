package com.example.meroPASAL.controller;

import com.example.meroPASAL.dto.LoginRequest;
import com.example.meroPASAL.model.user.Customer;
import com.example.meroPASAL.model.user.Role;
import com.example.meroPASAL.model.user.Shopkeeper;
import com.example.meroPASAL.model.user.User;
import com.example.meroPASAL.repository.RoleRepository;
import com.example.meroPASAL.repository.UserRepository;
import com.example.meroPASAL.response.SignUpResponse;
import com.example.meroPASAL.security.jwt.JwtUtils;
import com.example.meroPASAL.security.user.ShopUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/signup/customer")
    public ResponseEntity<SignUpResponse> signupCustomer(@Valid @RequestBody Customer customer) {
        if (userRepository.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest().body(new SignUpResponse("Email already exists"));
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        assignDefaultRole(customer, "CUSTOMER");

        userRepository.save(customer);
        return ResponseEntity.ok(new SignUpResponse("Customer registered successfully"));
    }

    @PostMapping("/signup/shopkeeper")
    public ResponseEntity<SignUpResponse> signupShopkeeper(@Valid @RequestBody Shopkeeper shopkeeper) {
        if (userRepository.existsByEmail(shopkeeper.getEmail())) {
            return ResponseEntity.badRequest().body(new SignUpResponse("Email already exists"));
        }

        shopkeeper.setPassword(passwordEncoder.encode(shopkeeper.getPassword()));
        assignDefaultRole(shopkeeper, "SHOPKEEPER");

        userRepository.save(shopkeeper);
        return ResponseEntity.ok(new SignUpResponse("Shopkeeper registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenForUser(authentication);

        return ResponseEntity.ok(new HashMap<>() {{
            put("token", jwt);
            put("email", userDetails.getEmail());
            put("roles", userDetails.getAuthorities());
        }});
    }

    private void assignDefaultRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        user.setRoles(Set.of(role));
    }
}
