package com.example.meroPASAL.security.service;

import com.example.meroPASAL.security.repository.UserRepository;
import com.example.meroPASAL.security.userModel.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Patterns for each field
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^\\d{10}$"); // Adjust to your format
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$"
    );

    public User getProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(String email, Map<String, String> updates) {
        User user = getProfile(email);

        // ✅ Name: letters & spaces, 2–50 chars
        if (updates.containsKey("name")) {
            String name = updates.get("name").trim();
            if (!NAME_PATTERN.matcher(name).matches()) {
                throw new RuntimeException("Name must be 2–50 alphabetic characters");
            }
            user.setName(name);
        }

        // ✅ Email: RFC-style basic validation
        if (updates.containsKey("email")) {
            String newEmail = updates.get("email").trim();
            if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
                throw new RuntimeException("Invalid email format");
            }
            user.setEmail(newEmail);
        }

        // ✅ Contact number: exactly 10 digits (adjust if your country differs)
        if (updates.containsKey("contactNumber")) {
            String contact = updates.get("contactNumber").trim();
            if (!CONTACT_PATTERN.matcher(contact).matches()) {
                throw new RuntimeException("Contact number must be exactly 10 digits");
            }
            user.setContactNumber(contact);
        }

        // ✅ Password: must match old password & new must match pattern
        if (updates.containsKey("currentPassword") && updates.containsKey("newPassword")) {
            String currentPassword = updates.get("currentPassword");
            String newPassword = updates.get("newPassword");

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                throw new RuntimeException(
                        "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
                );
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    /** Require current password for permanent deletion */
    public void deleteAccount(String email, String currentPassword) {
        User user = getProfile(email);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        userRepository.delete(user);
    }
}
