package com.example.meroPASAL.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String password) {
        return encoder.encode(password);
    }

    public static boolean matches(String password, String confirmPassword) {
        return encoder.matches(password, confirmPassword);
    }
}
