package com.powerme.service.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "12345678";
        String hash = encoder.encode(password);

        System.out.println("Hash for '" + password + "':");
        System.out.println(hash);
    }
}
