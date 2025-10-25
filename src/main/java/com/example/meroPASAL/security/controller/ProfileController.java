package com.example.meroPASAL.security.controller;

import com.example.meroPASAL.security.service.ProfileService;
import com.example.meroPASAL.security.userModel.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@PreAuthorize("hasAnyRole('CUSTOMER', 'SHOPKEEPER')")
@RestController
@RequestMapping("${api.prefix}/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    // ✅ Get user profile (excluding password)
    @GetMapping
    public ResponseEntity<User> getProfile(Principal principal) {
        return ResponseEntity.ok(profileService.getProfile(principal.getName()));
    }

    // ✅ Update user details (requires current password for password update)


    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            Principal principal,
            @RequestBody Map<String, String> updates
    ) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getName(), updates));
    }

    // ✅ Temporary logout (frontend will just delete token, no backend needed)

    // ✅ Permanent logout (delete user + all data)
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, String> payload) {

        String currentPassword = payload.get("currentPassword");
        if (currentPassword == null || currentPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Current password is required");
        }

        profileService.deleteAccount(principal.getUsername(), currentPassword);
        return ResponseEntity.ok("Account deleted");
    }
}
